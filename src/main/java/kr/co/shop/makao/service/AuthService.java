package kr.co.shop.makao.service;

import kr.co.shop.makao.component.AuthTokenManager;
import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthTokenManager authTokenManager;

    public AuthDTO.TokenIssueResponse issue(String subject) {
        String accessToken = authTokenManager.create(subject, TokenType.ACCESS_TOKEN);
        String refreshToken = authTokenManager.create(subject, TokenType.REFRESH_TOKEN);

        return AuthDTO.TokenIssueResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthDTO.TokenReissueResponse reissue(AuthDTO.TokenReissueRequest dto) {
        String subject = authTokenManager.getSubject(dto.refreshToken(), TokenType.REFRESH_TOKEN);
        var tokens = issue(subject);

        return AuthDTO.TokenReissueResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .build();
    }
}
