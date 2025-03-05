package kr.co.shop.makao.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.TokenExpiredException;
import kr.co.shop.makao.config.AuthProperties;
import kr.co.shop.makao.enums.TokenType;
import kr.co.shop.makao.response.CommonException;
import kr.co.shop.makao.vo.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtManager {
    private final AuthProperties authProperties;

    public String create(AuthUser payload, TokenType tokenType) {
        var algorithm = tokenType == TokenType.ACCESS_TOKEN ? authProperties.getAccessTokenAlgorithm() : authProperties.getRefreshTokenAlgorithm();
        var expiration = tokenType == TokenType.ACCESS_TOKEN ? authProperties.getAccessTokenExpiration() : authProperties.getRefreshTokenExpiration();
        return JWT.create()
                .withSubject(payload.email())
                .withClaim("id", payload.id())
                .withClaim("role", payload.role())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(algorithm);
    }

    public AuthUser getAuthUser(String token, TokenType tokenType) {
        try {
            var algorithm = tokenType == TokenType.ACCESS_TOKEN ? authProperties.getAccessTokenAlgorithm() : authProperties.getRefreshTokenAlgorithm();
            var jwt = JWT.require(algorithm)
                    .build()
                    .verify(token);

            return AuthUser.builder()
                    .email(jwt.getSubject())
                    .id(jwt.getClaim("id").asLong())
                    .role(jwt.getClaim("role").asString())
                    .build();
        } catch (TokenExpiredException cause) {
            throw CommonException.BAD_REQUEST.toException("EXPIRED_" + tokenType.name(), cause);
        } catch (Exception cause) {
            throw CommonException.BAD_REQUEST.toException("INVALID_" + tokenType.name(), cause);
        }
    }
}
