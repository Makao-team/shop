package kr.co.shop.makao.service;

import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.enums.UserRole;
import kr.co.shop.makao.response.CommonExceptionImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private UserService userService;
    @Mock
    private AuthTokenManager authTokenManager;

    @Test
    void signUp_성공() {
        var dto = AuthDTO.SignUpRequest.builder()
                .name("name")
                .email("email@test.com")
                .phoneNumber("010-1234-5678")
                .password("password123!")
                .role(UserRole.CUSTOMER)
                .build();

        doNothing().when(userService).save(dto);

        authService.signUp(dto);
    }

    @Nested
    class SignIn {
        AuthDTO.SignInRequest dto = AuthDTO.SignInRequest.builder()
                .email("email")
                .password("password")
                .build();

        @Test
        void signIn_성공() {
            when(userService.validateUser(dto.email(), dto.password())).thenReturn(true);
            when(authTokenManager.createAccessToken(dto.email())).thenReturn("accessToken");
            when(authTokenManager.createRefreshToken(dto.email())).thenReturn("refreshToken");

            var res = AuthDTO.SignInResponse.builder()
                    .accessToken("accessToken")
                    .refreshToken("refreshToken")
                    .build();

            assertThat(authService.signIn(dto)).isEqualTo(res);
        }

        @Test
        void signIn_검증_실패() {
            when(userService.validateUser(dto.email(), dto.password())).thenReturn(false);

            var exception = assertThrows(CommonExceptionImpl.class, () -> authService.signIn(dto));
            assertThat(exception.getMessage()).isEqualTo("AUTHENTICATION_FAILED");
        }
    }
}