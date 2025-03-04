package kr.co.shop.makao.controller;

import com.auth0.jwt.algorithms.Algorithm;
import io.restassured.http.ContentType;
import kr.co.shop.makao.config.AuthProperties;
import kr.co.shop.makao.config.PostgreInitializer;
import kr.co.shop.makao.dto.ProductDTO;
import kr.co.shop.makao.entity.User;
import kr.co.shop.makao.enums.UserRole;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@ContextConfiguration(initializers = PostgreInitializer.class)
class ProductControllerTest extends BaseIntegrationTest {
    @Autowired
    private AuthProperties authProperties;

    @Nested
    class save {
        Algorithm algorithm = authProperties.getAccessTokenAlgorithm();

        long expiration = 1000 * 60 * 60;

        private ProductDTO.CreateRequest createRequest(long merchantId) {
            return ProductDTO.CreateRequest.builder()
                    .name("상품")
                    .description("상품 설명")
                    .price(1000)
                    .stock(10)
                    .merchantId(merchantId)
                    .build();
        }

        @Test
        void save_MERCHANT_성공() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User merchant = findUserByEmail(email);
            String accessToken = createToken(algorithm, expiration, UserRole.MERCHANT, email, merchant.getId());

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(createRequest(merchant.getId()))
                    .when()
                    .post("/products")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"));
        }

        @Test
        void save_ADMIN_성공() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User merchant = findUserByEmail(email);
            String accessToken = createToken(algorithm, expiration, UserRole.ADMIN, email, merchant.getId());

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(createRequest(merchant.getId()))
                    .when()
                    .post("/products")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"));
        }

        @Test
        void save_CUSTOMER_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User merchant = findUserByEmail(email);
            String accessToken = createToken(algorithm, expiration, UserRole.CUSTOMER, email, merchant.getId());

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(createRequest(merchant.getId()))
                    .when()
                    .post("/products")
                    .then()
                    .statusCode(403)
                    .body("message", equalTo("FORBIDDEN"));
        }

        @Test
        void save_merchant_없음_실패() {
            String email = createRandomEmail();
            String accessToken = createToken(algorithm, expiration, UserRole.MERCHANT, email, new Random().nextLong());
            long wrongMerchantId = new Random().nextLong();

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(createRequest(wrongMerchantId))
                    .when()
                    .post("/products")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("MERCHANT_NOT_FOUND"));
        }

        @Test
        void save_다른_merchant_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User merchant = findUserByEmail(email);
            String anotherEmail = createRandomEmail();
            insertUser("user", anotherEmail, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User anotherMerchant = findUserByEmail(anotherEmail);
            String accessToken = createToken(algorithm, expiration, UserRole.MERCHANT, anotherEmail, anotherMerchant.getId());

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(createRequest(merchant.getId()))
                    .when()
                    .post("/products")
                    .then()
                    .statusCode(403)
                    .body("message", equalTo("FORBIDDEN"));
        }
    }
}