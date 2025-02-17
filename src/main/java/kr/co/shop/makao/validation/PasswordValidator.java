package kr.co.shop.makao.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return addConstraintViolation(context, "INVALID_PASSWORD_REQUIRED");
        }

        if (password.length() < 8) {
            return addConstraintViolation(context, "INVALID_PASSWORD_LENGTH");
        }

        if (!password.matches(".*[A-Za-z].*")) {
            return addConstraintViolation(context, "INVALID_PASSWORD_LETTER");
        }

        if (!password.matches(".*\\d.*")) {
            return addConstraintViolation(context, "INVALID_PASSWORD_DIGIT");
        }

        if (!password.matches(".*[@$!%*?&].*")) {
            return addConstraintViolation(context, "INVALID_PASSWORD_SPECIAL_CHAR");
        }

        return true;
    }

    private boolean addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
