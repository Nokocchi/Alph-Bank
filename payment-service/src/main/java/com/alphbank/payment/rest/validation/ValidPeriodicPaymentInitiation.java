package com.alphbank.payment.rest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.berlingroup.rest.model.PeriodicPaymentInitiationJsonDTO;

import java.lang.annotation.*;
import java.math.BigDecimal;

@Documented
@Constraint(validatedBy = ValidPeriodicPaymentInitiation.PeriodicPaymentInitiationValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPeriodicPaymentInitiation {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class PeriodicPaymentInitiationValidator implements ConstraintValidator<ValidPeriodicPaymentInitiation, PeriodicPaymentInitiationJsonDTO> {

        @Override
        public void initialize(ValidPeriodicPaymentInitiation contactNumber) {
        }

        @Override
        public boolean isValid(PeriodicPaymentInitiationJsonDTO paymentInitiation, ConstraintValidatorContext context) {
            //context.disableDefaultConstraintViolation();

            String amountString = paymentInitiation.getInstructedAmount().getAmount();

            try {
                new BigDecimal(amountString);
            } catch (NumberFormatException e) {
                context.buildConstraintViolationWithTemplate("Amount must be a number.")
                        .addPropertyNode("instructedAmount.amount")
                        .addConstraintViolation();
                return false;
            }

            return true;
        }
    }
}


