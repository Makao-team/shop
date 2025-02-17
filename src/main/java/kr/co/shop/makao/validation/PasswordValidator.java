package kr.co.shop.makao.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return addConstraintViolation(context, "INVALID_PASSWORD");
        }

        if (password.length() < 8) {
            return addConstraintViolation(context, "INVALID_PASSWORD");
        }

        if (!password.matches(".*[A-Za-z].*")) {
            return addConstraintViolation(context, "INVALID_PASSWORD");
        }

        if (!password.matches(".*\\d.*")) {
            return addConstraintViolation(context, "INVALID_PASSWORD");
        }

        if (!password.matches(".*[@$!%*?&].*")) {
            return addConstraintViolation(context, "INVALID_PASSWORD");
        }

        return true;
    }

    private boolean addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
