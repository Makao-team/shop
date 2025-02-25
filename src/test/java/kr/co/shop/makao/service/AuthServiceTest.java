package kr.co.shop.makao.service;

import kr.co.shop.makao.component.AuthTokenManager;
import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.enums.TokenType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private AuthTokenManager authTokenManager;

    @Nested
    class issue {
        @Test
        void issue_성공() {
            when(authTokenManager.create("email", TokenType.ACCESS_TOKEN)).thenReturn("accessToken");
            when(authTokenManager.create("email", TokenType.REFRESH_TOKEN)).thenReturn("refreshToken");

            var tokens = authService.issue("email");
            assertThat(tokens.accessToken()).isEqualTo("accessToken");
            assertThat(tokens.refreshToken()).isEqualTo("refreshToken");
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