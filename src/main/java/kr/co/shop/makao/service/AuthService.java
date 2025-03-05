package kr.co.shop.makao.service;

import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.enums.TokenType;
import kr.co.shop.makao.vo.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final JwtService jwtService;

    public AuthDTO.TokenIssueResponse issue(AuthUser authUser) {
        var accessToken = jwtService.create(authUser, TokenType.ACCESS_TOKEN);
        var refreshToken = jwtService.create(authUser, TokenType.REFRESH_TOKEN);

        return AuthDTO.TokenIssueResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthDTO.TokenReissueResponse reissue(AuthDTO.TokenReissueRequest dto) {
        var authUser = jwtService.getAuthUser(dto.refreshToken(), TokenType.REFRESH_TOKEN);
        var tokens = issue(authUser);

        return AuthDTO.TokenReissueResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .build();
    }
}
