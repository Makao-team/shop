package kr.co.shop.makao.service;

import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.enums.TokenType;
import kr.co.shop.makao.vo.AuthUser;
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
    private final AuthUser payload = AuthUser.builder()
            .email("email")
            .id(1)
            .role("role")
            .build();

    @InjectMocks
    private AuthService authService;
    @Mock
    private JwtService jwtService;

    @Nested
    class issue {
        @Test
        void issue_성공() {
            when(jwtService.create(payload, TokenType.ACCESS_TOKEN)).thenReturn("accessToken");
            when(jwtService.create(payload, TokenType.REFRESH_TOKEN)).thenReturn("refreshToken");

            var tokens = authService.issue(payload);
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
            when(jwtService.getAuthUser(dto.refreshToken(), TokenType.REFRESH_TOKEN)).thenReturn(payload);
            when(jwtService.create(payload, TokenType.ACCESS_TOKEN)).thenReturn("accessToken");

            assertThat(authService.reissue(dto).accessToken()).isEqualTo("accessToken");
        }
    }
}