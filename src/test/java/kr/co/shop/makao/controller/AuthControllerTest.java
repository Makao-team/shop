package kr.co.shop.makao.controller;

import com.auth0.jwt.algorithms.Algorithm;
import io.restassured.http.ContentType;
import kr.co.shop.makao.config.AuthProperties;
import kr.co.shop.makao.config.PostgreInitializer;
import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.enums.UserRole;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;

@ContextConfiguration(initializers = PostgreInitializer.class)
class AuthControllerTest extends BaseIntegrationTest {
    @Autowired
    private AuthProperties authProperties;

    @Nested
    class reissue {
        Algorithm algorithm = authProperties.getAccessTokenAlgorithm();
        String email = createRandomEmail();
        UserRole role = UserRole.MERCHANT;

        @Test
        void reissue_성공() {
            given().contentType(ContentType.JSON)
                    .body(AuthDTO.TokenReissueRequest.builder()
                            .refreshToken(createToken(algorithm, 10000, role, email, 1L))
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
                            .refreshToken(createToken(algorithm, -1000, role, email, 1L))
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
                            .refreshToken(createToken(Algorithm.HMAC384("wrongSecret"), 10000, role, email, 1L))
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