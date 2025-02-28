package kr.co.shop.makao.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.restassured.http.ContentType;
import kr.co.shop.makao.config.AuthProperties;
import kr.co.shop.makao.config.PostgreInitializer;
import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.util.RandomString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;

@ContextConfiguration(initializers = PostgreInitializer.class)
class AuthControllerTest extends IntegrationTest {
    @Autowired
    private AuthProperties authProperties;

    private String createRandomEmail() {
        return RandomString.generateEngDigit(30) + "@example.com";
    }

    @Nested
    class reissue {
        private String createToken(Algorithm algorithm, long expiration) {
            return JWT.create()
                    .withSubject(createRandomEmail())
                    .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                    .sign(algorithm);
        }

        @Test
        void reissue_성공() {
            given().contentType(ContentType.JSON)
                    .body(AuthDTO.TokenReissueRequest.builder()
                            .refreshToken(createToken(authProperties.getRefreshTokenAlgorithm(), 10000))
                            .build())
                    .when()
                    .post("/auth/token/reissue")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data.accessToken", any(String.class));
        }


        @Test
        void reissue_만료_실패() {
            given().contentType(ContentType.JSON)
                    .body(AuthDTO.TokenReissueRequest.builder()
                            .refreshToken(createToken(authProperties.getRefreshTokenAlgorithm(), -1000))
                            .build())
                    .when()
                    .post("/auth/token/reissue")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("EXPIRED_REFRESH_TOKEN"))
                    .body("data", equalTo(null));
        }

        @Test
        void reissue_잘못된_토큰_실패() {
            given().contentType(ContentType.JSON)
                    .body(AuthDTO.TokenReissueRequest.builder()
                            .refreshToken(createToken(Algorithm.none(), 10000))
                            .build())
                    .when()
                    .post("/auth/token/reissue")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("INVALID_REFRESH_TOKEN"))
                    .body("data", equalTo(null));
        }
    }
}