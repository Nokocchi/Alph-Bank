package com.alphbank.payment.rest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.berlingroup.rest.model.SigningBasketDTO;

import java.lang.annotation.*;
import java.util.List;
import java.util.UUID;

@Documented
@Constraint(validatedBy = UUIDNotNullPaymentIds.PaymentIdsValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UUIDNotNullPaymentIds {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class PaymentIdsValidator implements ConstraintValidator<UUIDNotNullPaymentIds, SigningBasketDTO> {

        @Override
        public void initialize(UUIDNotNullPaymentIds contactNumber) {
        }

        @Override
        public boolean isValid(SigningBasketDTO paymentInitiation, ConstraintValidatorContext context) {
            context.disableDefaultConstraintViolation();

            List<String> paymentIds = paymentInitiation.getPaymentIds();

            if (paymentIds == null) {
                context.buildConstraintViolationWithTemplate("Must not be null.")
                        .addPropertyNode("paymentIds")
                        .addConstraintViolation();
                return false;
            }

            try {
                List<UUID> uuids = paymentIds.stream().map(UUID::fromString).toList();
            } catch (IllegalArgumentException e) {
                context.buildConstraintViolationWithTemplate("Payment IDs must be UUIDs.")
                        .addPropertyNode("paymentIds")
                        .addConstraintViolation();
                return false;
            }

            return true;
        }
    }
}


