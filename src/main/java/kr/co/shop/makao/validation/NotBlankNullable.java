package kr.co.shop.makao.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a field as not blank nullable.
 * Used in patch dto to allow null values.
 */
@Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBlankNullableValidator.class)
public @interface NotBlankNullable {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
