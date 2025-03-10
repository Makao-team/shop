package kr.co.shop.makao.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.shop.makao.enums.UserRole;
import kr.co.shop.makao.validation.Password;
import lombok.Builder;

public record UserDTO() {
    @Builder
    public record SaveRequest(
            @NotBlank(message = "BLANK_NAME")
            String name,
            @NotBlank(message = "INVALID_EMAIL")
            @Email(message = "INVALID_EMAIL")
            String email,
            @NotBlank(message = "BLANK_PHONE_NUMBER")
            String phoneNumber,
            @Password
            String password,
            @NotNull(message = "INVALID_USER_ROLE")
            UserRole role
    ) {
    }

    @Builder
    public record SignInRequest(
            @Email(message = "INVALID_EMAIL")
            String email,
            @Password
            String password
    ) {
    }

    @Builder
    public record SignInResponse(
            String accessToken,
            String refreshToken,
            UserRole role
    ) {
    }

}
