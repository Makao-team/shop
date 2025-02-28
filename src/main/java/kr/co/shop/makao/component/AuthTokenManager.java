package kr.co.shop.makao.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.TokenExpiredException;
import kr.co.shop.makao.config.AuthProperties;
import kr.co.shop.makao.enums.TokenType;
import kr.co.shop.makao.response.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
@Component
public class AuthTokenManager {
    private final AuthProperties authProperties;

    public String create(String subject, TokenType tokenType) {
        var algorithm = tokenType == TokenType.ACCESS_TOKEN ? authProperties.getAccessTokenAlgorithm() : authProperties.getRefreshTokenAlgorithm();
        var expiration = tokenType == TokenType.ACCESS_TOKEN ? authProperties.getAccessTokenExpiration() : authProperties.getRefreshTokenExpiration();
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(algorithm);
    }

    public String getSubject(String token, TokenType tokenType) {
        try {
            var algorithm = tokenType == TokenType.ACCESS_TOKEN ? authProperties.getAccessTokenAlgorithm() : authProperties.getRefreshTokenAlgorithm();
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (TokenExpiredException cause) {
            throw CommonException.BAD_REQUEST.toException("EXPIRED_REFRESH_TOKEN", cause);
        } catch (Exception cause) {
            throw CommonException.BAD_REQUEST.toException("INVALID_REFRESH_TOKEN", cause);
        }
    }
}
