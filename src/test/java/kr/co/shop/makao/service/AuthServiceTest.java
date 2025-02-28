package kr.co.shop.makao.service;

import kr.co.shop.makao.component.AuthTokenManager;
import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.entity.User;
import kr.co.shop.makao.enums.TokenType;
import kr.co.shop.makao.enums.UserRole;
import kr.co.shop.makao.response.CommonExceptionImpl;
import kr.co.shop.makao.util.StringEncoder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
                .password("password123!")
                .build();
        User user = User.builder().password("hashed").build();

        @Test
        void signIn_성공() {
            when(userService.findByEmail(dto.email())).thenReturn(user);
            try (var stringEncoderMockedStatic = mockStatic(StringEncoder.class)) {
                stringEncoderMockedStatic.when(() -> StringEncoder.match(dto.password(), user.getPassword())).thenReturn(true);
                when(authTokenManager.create(dto.email(), TokenType.ACCESS_TOKEN)).thenReturn("accessToken");
                when(authTokenManager.create(dto.email(), TokenType.REFRESH_TOKEN)).thenReturn("refreshToken");

                var res = AuthDTO.SignInResponse.builder()
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .role(user.getRole())
                        .build();

                assertThat(authService.signIn(dto)).isEqualTo(res);
            }
        }

        @Test
        void signIn_검증_실패() {
            when(userService.findByEmail(dto.email())).thenReturn(user);
            try (var stringEncoderMockedStatic = mockStatic(StringEncoder.class)) {
                stringEncoderMockedStatic.when(() -> StringEncoder.match(dto.password(), user.getPassword())).thenReturn(false);
                var exception = assertThrows(CommonExceptionImpl.class, () -> authService.signIn(dto));
                assertThat(exception.getMessage()).isEqualTo("AUTHENTICATION_FAILED");
            }
        }
    }

    @Nested
    class reissue {
        AuthDTO.TokenReissueRequest dto = AuthDTO.TokenReissueRequest.builder()
                .refreshToken("refreshToken")
                .build();

        @Test
        void reissue_성공() {
            when(authTokenManager.getSubject(dto.refreshToken(), TokenType.REFRESH_TOKEN)).thenReturn("email");
            when(authTokenManager.create("email", TokenType.ACCESS_TOKEN)).thenReturn("accessToken");

            assertThat(authService.reissue(dto).accessToken()).isEqualTo("accessToken");
        }
    }
}