package kr.co.shop.makao.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import kr.co.shop.makao.response.CommonException;

public enum UserRole {
    MERCHANT("merchant"),
    CUSTOMER("customer");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    @JsonCreator
    public static UserRole fromRole(String role) {
        for (UserRole userRole : values()) {
            if (userRole.role.equals(role)) {
                return userRole;
            }
        }

        throw CommonException.BAD_REQUEST.toException("INVALID_USER_ROLE");
    }

    @JsonValue
    public String getRole() {
        return role;
    }
}
