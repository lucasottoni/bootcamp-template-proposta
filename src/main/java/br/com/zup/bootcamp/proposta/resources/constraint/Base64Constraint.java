package br.com.zup.bootcamp.proposta.resources.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Base64;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Constraint(validatedBy = Base64Constraint.Validator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Base64Constraint {

    String message() default  "{br.com.zup.bootcamp.proposta.constraint.Base64Constraint.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    static class Validator implements ConstraintValidator<Base64Constraint, String> {

        private final Logger logger = LoggerFactory.getLogger(Validator.class);

        @Override
        public boolean isValid(String str, ConstraintValidatorContext constraintValidatorContext) {
            if (str == null) return true;

            return isValidBase64(str);
        }

        private boolean isValidBase64(String base64) {
            try {
                byte[] decode = Base64.getDecoder().decode(base64.getBytes());
                return decode != null && decode.length > 0;
            } catch (Exception ex) {
                logger.warn("Failure to check biometric encoding", ex);
                return false;
            }
        }
    }
}
