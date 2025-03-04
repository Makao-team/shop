package kr.co.shop.makao.resolver;

import kr.co.shop.makao.enums.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Available
 * <p>
 * Annotation for checking if the user has the required role to access the endpoint.
 * The roles are defined in the {@link UserRole} enum.
 * The annotation is used in the {@link kr.co.shop.makao.resolver.AuthHandlerMethodArgumentResolver} class.
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Available {
    UserRole[] roles();
}
