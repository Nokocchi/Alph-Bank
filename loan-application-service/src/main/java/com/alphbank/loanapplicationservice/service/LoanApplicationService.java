package com.alphbank.loanapplicationservice.service;

import com.alphbank.loanapplicationservice.rest.model.ApplicationStatus;
import com.alphbank.loanapplicationservice.rest.model.CreateLoanApplicationRequest;
import com.alphbank.loanapplicationservice.rest.model.CreateLoanApplicationResponse;
import com.alphbank.loanapplicationservice.rest.model.LoanApplication;
import com.alphbank.loanapplicationservice.service.amqp.configuration.RabbitConfigurationProperties;
import com.alphbank.loanapplicationservice.service.amqp.model.SigningStatus;
import com.alphbank.loanapplicationservice.service.client.coreloanservice.CoreLoanServiceClient;
import com.alphbank.loanapplicationservice.service.client.coreloanservice.model.CreateLoanRequest;
import com.alphbank.loanapplicationservice.service.client.signingservice.SigningServiceClient;
import com.alphbank.loanapplicationservice.service.client.signingservice.configuration.SigningServiceClientConfigurationProperties;
import com.alphbank.loanapplicationservice.service.client.signingservice.model.SetupSigningSessionRequest;
import com.alphbank.loanapplicationservice.service.client.signingservice.model.SetupSigningSessionResponse;
import com.alphbank.loanapplicationservice.service.error.InvalidLoanApplicationSearchException;
import com.alphbank.loanapplicationservice.service.error.LoanApplicationNotFoundException;
import com.alphbank.loanapplicationservice.service.repository.LoanApplicationRepository;
import com.alphbank.loanapplicationservice.service.repository.model.LoanApplicationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final RabbitConfigurationProperties rabbitConfigurationProperties;
    private final SigningServiceClient signingServiceClient;
    private final SigningServiceClientConfigurationProperties signingServiceProperties;
    private final CoreLoanServiceClient coreLoanServiceClient;


    public Flux<LoanApplication> findAllLoanApplicationsByCustomerIdOrAccountId(UUID customerId, UUID accountId) {
        if (customerId == null && accountId == null) {
            throw new InvalidLoanApplicationSearchException("Both customerId and accountId are null.");
        }
        if (customerId != null && accountId != null) {
            throw new InvalidLoanApplicationSearchException("Both customerId and accountId are set.");
        }
        return Mono.justOrEmpty(customerId)
                .flatMapMany(loanApplicationRepository::findAllLoanApplicationsByCustomerId)
                .switchIfEmpty(loanApplicationRepository.findAllLoanApplicationsByAccountId(accountId))
                .map(this::convertToRestModel);
    }

    public Mono<CreateLoanApplicationResponse> createLoanApplication(CreateLoanApplicationRequest createLoanApplicationRequest) {
        LoanApplicationEntity loanApplicationEntity = LoanApplicationEntity.from(createLoanApplicationRequest);
        return loanApplicationRepository.save(loanApplicationEntity)
                .flatMap(persistedEntity -> setupSigningSession(persistedEntity, createLoanApplicationRequest));
    }

    private Mono<CreateLoanApplicationResponse> setupSigningSession(LoanApplicationEntity loanApplicationEntity, CreateLoanApplicationRequest createLoanApplicationRequest) {
        SetupSigningSessionRequest setupSigningSessionRequest = createSetupSigningSessionRequest(createLoanApplicationRequest);
        return signingServiceClient.setupSigningSession(setupSigningSessionRequest)
                .flatMap(signingSession -> persistSigningSessionData(loanApplicationEntity, signingSession));
    }

    private Mono<CreateLoanApplicationResponse> persistSigningSessionData(LoanApplicationEntity loanApplicationEntity, SetupSigningSessionResponse signingSession) {
        LoanApplicationEntity updatedLoanApplicationEntity = loanApplicationEntity.withSigningSessionId(signingSession.signingSessionId());
        return loanApplicationRepository.save(updatedLoanApplicationEntity)
                .thenReturn(new CreateLoanApplicationResponse(signingSession.signingUrl()));
    }

    public Mono<LoanApplication> getLoanApplication(UUID loanApplicationId) {
        return loanApplicationRepository.findById(loanApplicationId)
                .map(this::convertToRestModel)
                .switchIfEmpty(Mono.error(new LoanApplicationNotFoundException()));
    }

    public Mono<Void> updateLoanApplicationStatus(UUID signingSessionId, SigningStatus signingStatus) {
        ApplicationStatus status = fromSigningStatus(signingStatus);
        if(status == null){
            return Mono.empty();
        }

        // TODO: If stauts is complete, send request to coreLoanServiceClient
        return loanApplicationRepository.findBySigningSessionId(signingSessionId)
                .doOnNext(loanApplicationEntity -> log.info("Setting loan application with id {} and signingSessionId {} to status {}",
                        loanApplicationEntity.getLoanApplicationId(),
                        loanApplicationEntity.getSigningSessionId(),
                        status))
                .map(loanApplicationEntity -> loanApplicationEntity.withApplicationStatus(status.toString()))
                .flatMap(loanApplicationRepository::save)
                .flatMap(this::handleUpdatedApplicationStatus);
    }

    private Mono<Void> handleUpdatedApplicationStatus(LoanApplicationEntity entity){
        ApplicationStatus newlySavedStatus = ApplicationStatus.valueOf(entity.getApplicationStatus());
        if(newlySavedStatus == ApplicationStatus.CREDIT_CHECK_PENDING){
            return coreLoanServiceClient.createLoan(fromEntity(entity))
                    .then(loanApplicationRepository.save(entity.withApplicationStatus(ApplicationStatus.APPROVED.toString())))
                    .then();
        }
        else return Mono.empty();
    }

    private CreateLoanRequest fromEntity(LoanApplicationEntity entity){
        Money principal = Money.of(entity.getPrincipal(), entity.getCurrency());
        return new CreateLoanRequest(entity.getCustomerId(), entity.getAccountId(), principal, entity.getFixedRateInterestAPR(), entity.getLoanPeriodMonths());
    }

    private ApplicationStatus fromSigningStatus(SigningStatus signingStatus) {
        return switch (signingStatus) {
            case IN_PROGRESS -> ApplicationStatus.SIGNING_STARTED;
            case CANCELLED, EXPIRED -> ApplicationStatus.SIGNING_FAILED;
            case COMPLETED -> ApplicationStatus.CREDIT_CHECK_PENDING;
            default -> null;
        };
    }

    private LoanApplication convertToRestModel(LoanApplicationEntity loanApplicationEntity) {
        return LoanApplication.builder()
                .loanApplicationId(loanApplicationEntity.getLoanApplicationId())
                .customerId(loanApplicationEntity.getCustomerId())
                .accountId(loanApplicationEntity.getAccountId())
                .governmentId(loanApplicationEntity.getGovernmentId())
                .locale(new Locale(loanApplicationEntity.getLocale(), loanApplicationEntity.getCountryCode()))
                .principal(Money.of(loanApplicationEntity.getPrincipal(), loanApplicationEntity.getCurrency()))
                .fixedRateInterestAPR(loanApplicationEntity.getFixedRateInterestAPR())
                .loanTermMonths(loanApplicationEntity.getLoanPeriodMonths())
                .applicationStatus(ApplicationStatus.valueOf(loanApplicationEntity.getApplicationStatus()))
                .build();
    }


    private SetupSigningSessionRequest createSetupSigningSessionRequest(CreateLoanApplicationRequest createLoanApplicationRequest) {
        return new SetupSigningSessionRequest(
                createLoanApplicationRequest.customerId(),
                createLoanApplicationRequest.governmentId(),
                createLoanApplicationRequest.locale(),
                rabbitConfigurationProperties.getLoanApplicationSigningStatusRoutingKey(),
                getDocumentToSign(createLoanApplicationRequest),
                createLoanApplicationRequest.onSigningSuccessRedirectUrl(),
                createLoanApplicationRequest.onSigningFailedRedirectUrl()
        );
    }

    private String getDocumentToSign(CreateLoanApplicationRequest createLoanApplicationRequest) {
        String template = signingServiceProperties.getDocumentToSignTemplate();
        BigDecimal principalAmount = createLoanApplicationRequest.principal().getNumber().numberValue(BigDecimal.class);
        MonetaryAmount totalCost = getTotalCostOfLoan(createLoanApplicationRequest.principal(), createLoanApplicationRequest.fixedRateInterestAPR(), createLoanApplicationRequest.loanTermMonths());
        BigDecimal totalCostAmount = totalCost.getNumber().numberValue(BigDecimal.class);
        String currencyCode = createLoanApplicationRequest.principal().getCurrency().getCurrencyCode();

        return String.format(template,
                principalAmount,
                currencyCode,
                createLoanApplicationRequest.fixedRateInterestAPR(),
                createLoanApplicationRequest.loanTermMonths(),
                totalCostAmount,
                currencyCode,
                totalCostAmount.subtract(principalAmount),
                currencyCode);
    }

    private MonetaryAmount getTotalCostOfLoan(MonetaryAmount principal, BigDecimal fixedRateInterestAPR, int loanTermMonths) {
        BigDecimal principalAmount = principal.getNumber().numberValue(BigDecimal.class);

        BigDecimal monthlyInterestRate = fixedRateInterestAPR
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_EVEN)
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN);
        BigDecimal percentOverEntireLoanTerm = monthlyInterestRate.add(BigDecimal.ONE).pow(loanTermMonths);

        BigDecimal numerator = monthlyInterestRate.multiply(percentOverEntireLoanTerm).multiply(principalAmount);
        BigDecimal denominator = percentOverEntireLoanTerm.subtract(BigDecimal.ONE);

        BigDecimal monthlyInstallment = numerator.divide(denominator, 6, RoundingMode.HALF_EVEN);
        BigDecimal totalCost = monthlyInstallment.multiply(BigDecimal.valueOf(loanTermMonths));

        return Money.of(totalCost, principal.getCurrency());
    }

}
