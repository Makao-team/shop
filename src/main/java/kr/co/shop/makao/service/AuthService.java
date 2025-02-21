package kr.co.shop.makao.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import kr.co.shop.makao.component.AuthTokenManager;
import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.enums.TokenType;
import kr.co.shop.makao.response.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserService userService;
    private final AuthTokenManager authTokenManager;

    @Transactional
    public void signUp(AuthDTO.SignUpRequest dto) {
        userService.save(dto);
    }

    @Transactional(readOnly = true)
    public AuthDTO.SignInResponse signIn(AuthDTO.SignInRequest dto) {
        if (!userService.verifyUser(dto.email(), dto.password()))
            throw CommonException.BAD_REQUEST.toException("AUTHENTICATION_FAILED");

        String accessToken = authTokenManager.create(dto.email(), TokenType.ACCESS_TOKEN);
        String refreshToken = authTokenManager.create(dto.email(), TokenType.REFRESH_TOKEN);

        return AuthDTO.SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthDTO.TokenReissueResponse reissue(AuthDTO.TokenReissueRequest dto) {
        try {
            String email = authTokenManager.getSubject(dto.refreshToken(), TokenType.REFRESH_TOKEN);
            String accessToken = authTokenManager.create(email, TokenType.ACCESS_TOKEN);

            return AuthDTO.TokenReissueResponse.builder()
                    .accessToken(accessToken)
                    .build();
        } catch (ExpiredJwtException cause) {
            throw CommonException.BAD_REQUEST.toException("EXPIRED_REFRESH_TOKEN", cause);
        } catch (SignatureException cause) {
            throw CommonException.BAD_REQUEST.toException("INVALID_REFRESH_TOKEN", cause);
        }
    }
}
