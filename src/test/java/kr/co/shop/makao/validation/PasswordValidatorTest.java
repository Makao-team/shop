package kr.co.shop.makao.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PasswordValidatorTest {

    private PasswordValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
        context = mock(ConstraintValidatorContext.class);
        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));
    }

    @Test
    void 유효한_비밀번호_테스트() {
        assertTrue(validator.isValid("Test123!", context));
        assertTrue(validator.isValid("Secure@99", context));
    }

    @Test
    void 비밀번호_공백_또는_널_테스트() {
        assertFalse(validator.isValid("", context));
        assertFalse(validator.isValid(null, context));

        verify(context, times(2)).disableDefaultConstraintViolation();
        verify(context, times(2)).buildConstraintViolationWithTemplate("INVALID_PASSWORD");
    }

    @Test
    void 비밀번호_길이_부족_테스트() {
        assertFalse(validator.isValid("Short1!", context));

        verify(context, times(1)).disableDefaultConstraintViolation();
        verify(context, times(1)).buildConstraintViolationWithTemplate("INVALID_PASSWORD");
    }

    @Test
    void 비밀번호_영문_누락_테스트() {
        assertFalse(validator.isValid("12345678!", context));

        verify(context, times(1)).disableDefaultConstraintViolation();
        verify(context, times(1)).buildConstraintViolationWithTemplate("INVALID_PASSWORD");
    }

    @Test
    void 비밀번호_숫자_누락_테스트() {
        assertFalse(validator.isValid("Password!", context));

        verify(context, times(1)).disableDefaultConstraintViolation();
        verify(context, times(1)).buildConstraintViolationWithTemplate("INVALID_PASSWORD");
    }

    @Test
    void 비밀번호_특수문자_누락_테스트() {
        assertFalse(validator.isValid("Test1234", context));

        verify(context, times(1)).disableDefaultConstraintViolation();
        verify(context, times(1)).buildConstraintViolationWithTemplate("INVALID_PASSWORD");
    }
}
