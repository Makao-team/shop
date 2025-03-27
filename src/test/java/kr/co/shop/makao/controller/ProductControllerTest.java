package kr.co.shop.makao.controller;

import io.restassured.http.ContentType;
import kr.co.shop.makao.config.AuthProperties;
import kr.co.shop.makao.config.PostgreInitializer;
import kr.co.shop.makao.dto.ProductDTO;
import kr.co.shop.makao.entity.Product;
import kr.co.shop.makao.entity.User;
import kr.co.shop.makao.enums.ProductStatus;
import kr.co.shop.makao.enums.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@ContextConfiguration(initializers = PostgreInitializer.class)
class ProductControllerTest extends BaseIntegrationTest {
    long expiration = 1000 * 60 * 60;
    @Autowired
    private AuthProperties authProperties;

    private Product insertProduct(String name, long merchantId) {
        return transactionTemplate.execute(status -> {
            em.createQuery("INSERT INTO product (name, description, price, stock, status, merchantId) VALUES (:name, \"상품 설명\", 1000, 10, :status, :merchantId)")
                    .setParameter("name", name)
                    .setParameter("status", ProductStatus.PENDING.getValue())
                    .setParameter("merchantId", merchantId)
                    .executeUpdate();
            return em.createQuery("SELECT p FROM product p WHERE p.merchantId = :merchantId", Product.class)
                    .setParameter("merchantId", merchantId)
                    .getSingleResult();
        });
    }

    private Product insertProduct(String name, long merchantId, ProductStatus productStatus) {
        return transactionTemplate.execute(transactionStatus -> {
            em.createQuery("INSERT INTO product (name, description, price, stock, status, merchantId) VALUES (:name, \"상품 설명\", 1000, 10, :status, :merchantId)")
                    .setParameter("name", name)
                    .setParameter("status", productStatus.getValue())
                    .setParameter("merchantId", merchantId)
                    .executeUpdate();
            return em.createQuery("SELECT p FROM product p WHERE p.merchantId = :merchantId", Product.class)
                    .setParameter("merchantId", merchantId)
                    .getSingleResult();
        });
    }

    private Product insertProduct(String name, long merchantId, ProductStatus productStatus, int stock) {
        return transactionTemplate.execute(transactionStatus -> {
            em.createQuery("INSERT INTO product (name, description, price, stock, status, merchantId) VALUES (:name, \"상품 설명\", 1000, :stock, :status, :merchantId)")
                    .setParameter("name", name)
                    .setParameter("status", productStatus.getValue())
                    .setParameter("merchantId", merchantId)
                    .setParameter("stock", stock)
                    .executeUpdate();
            return em.createQuery("SELECT p FROM product p WHERE p.merchantId = :merchantId", Product.class)
                    .setParameter("merchantId", merchantId)
                    .getSingleResult();
        });
    }

    @Nested
    class save {
        private ProductDTO.SaveRequest createRequest(long merchantId) {
            return ProductDTO.SaveRequest.builder()
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
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, merchant.getId());

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
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.ADMIN, email, merchant.getId());

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
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.CUSTOMER, email, merchant.getId());

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
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, new Random().nextLong());
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
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, anotherEmail, anotherMerchant.getId());

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

    @Nested
    class updateStatus {
        @Test
        void updateStatus_성공() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User merchant = findUserByEmail(email);
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, merchant.getId());
            Product product = insertProduct("상품", merchant.getId());

            ProductDTO.UpdateStatusRequest updateStatusRequest = ProductDTO.UpdateStatusRequest.builder()
                    .status(ProductStatus.ACTIVE)
                    .build();

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(updateStatusRequest)
                    .when()
                    .post("/products/" + product.getId() + "/status")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"));
        }

        @Test
        void updateStatus_제품_없음_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User merchant = findUserByEmail(email);
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, merchant.getId());
            long wrongProductId = new Random().nextLong();

            ProductDTO.UpdateStatusRequest updateStatusRequest = ProductDTO.UpdateStatusRequest.builder()
                    .status(ProductStatus.ACTIVE)
                    .build();

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(updateStatusRequest)
                    .when()
                    .post("/products/" + wrongProductId + "/status")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("PRODUCT_NOT_FOUND"));
        }

        @Test
        void updateStatus_다른_merchant_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User merchant = findUserByEmail(email);
            String anotherEmail = createRandomEmail();
            insertUser("user", anotherEmail, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User anotherMerchant = findUserByEmail(anotherEmail);
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, anotherEmail, anotherMerchant.getId());
            Product product = insertProduct("상품", merchant.getId());

            ProductDTO.UpdateStatusRequest updateStatusRequest = ProductDTO.UpdateStatusRequest.builder()
                    .status(ProductStatus.ACTIVE)
                    .build();

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(updateStatusRequest)
                    .when()
                    .post("/products/" + product.getId() + "/status")
                    .then()
                    .statusCode(403)
                    .body("message", equalTo("FORBIDDEN"));
        }
    }

    @Nested
    class update {
        @Test
        void update_성공() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User merchant = findUserByEmail(email);
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, merchant.getId());
            Product product = insertProduct("상품", merchant.getId());

            ProductDTO.UpdateRequest updateRequest = ProductDTO.UpdateRequest.builder()
                    .name(Optional.of("상품 수정"))
                    .description(Optional.of("상품 설명 수정"))
                    .price(Optional.of(2000))
                    .stock(Optional.of(20))
                    .build();

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(updateRequest)
                    .when()
                    .patch("/products/" + product.getId())
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"));
        }

        @Test
        void update_제품_없음_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User merchant = findUserByEmail(email);
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, merchant.getId());
            long wrongProductId = new Random().nextLong();

            ProductDTO.UpdateRequest updateRequest = ProductDTO.UpdateRequest.builder()
                    .name(Optional.of("상품 수정"))
                    .description(Optional.of("상품 설명 수정"))
                    .price(Optional.of(2000))
                    .stock(Optional.of(20))
                    .build();

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(updateRequest)
                    .when()
                    .patch("/products/" + wrongProductId)
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("PRODUCT_NOT_FOUND"));
        }

        @Test
        void update_제품_활성화_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User merchant = findUserByEmail(email);
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, merchant.getId());
            Product product = insertProduct("상품", merchant.getId(), ProductStatus.ACTIVE);

            ProductDTO.UpdateRequest updateRequest = ProductDTO.UpdateRequest.builder()
                    .name(Optional.of("상품 수정"))
                    .description(Optional.of("상품 설명 수정"))
                    .price(Optional.of(2000))
                    .stock(Optional.of(20))
                    .build();

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(updateRequest)
                    .when()
                    .patch("/products/" + product.getId())
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("PRODUCT_MUST_BE_PENDING"));
        }

        @Test
        void update_다른_merchant_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User merchant = findUserByEmail(email);
            String anotherEmail = createRandomEmail();
            insertUser("user", anotherEmail, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            User anotherMerchant = findUserByEmail(anotherEmail);
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, anotherEmail, anotherMerchant.getId());
            Product product = insertProduct("상품", merchant.getId());

            ProductDTO.UpdateRequest updateRequest = ProductDTO.UpdateRequest.builder()
                    .name(Optional.of("상품 수정"))
                    .description(Optional.of("상품 설명 수정"))
                    .price(Optional.of(2000))
                    .stock(Optional.of(20))
                    .build();

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(updateRequest)
                    .when()
                    .patch("/products/" + product.getId())
                    .then()
                    .statusCode(403)
                    .body("message", equalTo("FORBIDDEN"));
        }
    }

    @Nested
    class findAllDetail {
        @Test
        void findAllDetail_성공() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long merchantId = findUserByEmail(email).getId();
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, merchantId);
            insertProduct("상품", merchantId);

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .get("/products?merchantId=" + merchantId)
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data.contents.size()", greaterThan(0));
        }

        @Test
        void findAllDetail_customer_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.CUSTOMER);
            long customerId = findUserByEmail(email).getId();
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.CUSTOMER, email, customerId);

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .get("/products?merchantId=" + customerId)
                    .then()
                    .statusCode(403)
                    .body("message", equalTo("FORBIDDEN"));
        }

        @Test
        void findAllDetail_name_조회_성공() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long merchantId = findUserByEmail(email).getId();
            insertProduct("상품", merchantId);

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, merchantId))
                    .when()
                    .get("/products?filter=name&keyword=상품&merchantId=" + merchantId)
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data.contents.size()", greaterThan(0));
        }
    }

    @Nested
    class findOneDetail {
        @Test
        void findOneDetail_성공() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long merchantId = findUserByEmail(email).getId();
            Product product = insertProduct("상품", merchantId);

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, merchantId))
                    .when()
                    .get("/products/" + product.getId())
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"));
        }

        @Test
        void findOneDetail_제품_없음_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long wrongProductId = new Random().nextInt(10000000);

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, new Random().nextLong()))
                    .when()
                    .get("/products/" + wrongProductId)
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("PRODUCT_NOT_FOUND"));
        }

        @Test
        void findOneDetail_customer_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.CUSTOMER);
            long customerId = findUserByEmail(email).getId();
            Product product = insertProduct("상품", customerId);

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.CUSTOMER, email, customerId))
                    .when()
                    .get("/products/" + product.getId())
                    .then()
                    .statusCode(403)
                    .body("message", equalTo("FORBIDDEN"));
        }
    }

    @Nested
    class findAllView {
        @Test
        void findAllView_회원_성공() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long merchantId = findUserByEmail(email).getId();
            String accessToken = createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, merchantId);
            insertProduct("상품", merchantId);

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .get("/products/view")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data.contents.size()", greaterThan(0));
        }

        @Test
        void findAllView_비회원_성공() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long merchantId = findUserByEmail(email).getId();
            insertProduct("상품", merchantId);

            given().contentType(ContentType.JSON)
                    .when()
                    .get("/products/view")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data.contents.size()", greaterThan(0));
        }

        @Test
        void findAllView_name_조회_성공() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long merchantId = findUserByEmail(email).getId();
            insertProduct("상품", merchantId);

            given().contentType(ContentType.JSON)
                    .when()
                    .get("/products/view?filter=name&keyword=상품")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data.contents.size()", greaterThan(0));
        }
    }

    @Nested
    class findOneView {
        @Test
        void findOneView_성공() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long merchantId = findUserByEmail(email).getId();
            Product product = insertProduct("상품", merchantId);

            given().contentType(ContentType.JSON)
                    .when()
                    .get("/products/view/" + product.getId())
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"));
        }

        @Test
        void findOneView_제품_없음_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long wrongProductId = new Random().nextInt(10000000);

            given().contentType(ContentType.JSON)
                    .when()
                    .get("/products/view/" + wrongProductId)
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("PRODUCT_NOT_FOUND"));
        }
    }

    @Nested
    class archive {
        @Test
        void archive_성공() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long merchantId = findUserByEmail(email).getId();
            Product product = insertProduct("상품", merchantId);

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, merchantId))
                    .when()
                    .post("/products/archive/" + product.getId())
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"));
        }

        @Test
        void archive_제품_없음_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long wrongProductId = new Random().nextInt(10000000);

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, new Random().nextLong()))
                    .when()
                    .post("/products/archive/" + wrongProductId)
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("PRODUCT_NOT_FOUND"));
        }

        @Test
        void archive_제품_활성화_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long merchantId = findUserByEmail(email).getId();
            Product product = insertProduct("상품", merchantId, ProductStatus.ACTIVE);

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, email, merchantId))
                    .when()
                    .post("/products/archive/" + product.getId())
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("PRODUCT_MUST_BE_PENDING"));
        }

        @Test
        void archive_다른_merchant_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long merchantId = findUserByEmail(email).getId();
            Product product = insertProduct("상품", merchantId);
            String anotherEmail = createRandomEmail();
            insertUser("user", anotherEmail, createRandomPhoneNumber(), "password", UserRole.MERCHANT);
            long anotherMerchantId = findUserByEmail(anotherEmail).getId();

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.MERCHANT, anotherEmail, anotherMerchantId))
                    .when()
                    .post("/products/archive/" + product.getId())
                    .then()
                    .statusCode(403)
                    .body("message", equalTo("FORBIDDEN"));
        }
    }

    @Nested
    class deduct {
        @Test
        void deduct_성공() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.CUSTOMER);
            long customerId = findUserByEmail(email).getId();
            Product product = insertProduct("상품", customerId, ProductStatus.ACTIVE, 1000);

            ProductDTO.DeductRequest deductRequest = ProductDTO.DeductRequest.builder()
                    .quantity(1000)
                    .build();

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.CUSTOMER, email, customerId))
                    .body(deductRequest)
                    .when()
                    .post("/products/deduction/" + product.getId())
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"));
        }

        @Test
        void deduct_동시_성공() throws InterruptedException {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.CUSTOMER);
            long customerId = findUserByEmail(email).getId();
            Product product = insertProduct("상품", customerId, ProductStatus.ACTIVE, 1000);

            ProductDTO.DeductRequest deductRequest = ProductDTO.DeductRequest.builder()
                    .quantity(10)
                    .build();

            ExecutorService executorService = Executors.newFixedThreadPool(10);
            CountDownLatch latch = new CountDownLatch(10);

            List<Callable<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                tasks.add(() -> {
                    try {
                        given().contentType(ContentType.JSON)
                                .header("Authorization", "Bearer " + createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.CUSTOMER, email, customerId))
                                .body(deductRequest)
                                .when()
                                .post("/products/deduction/" + product.getId())
                                .then()
                                .statusCode(200)
                                .body("message", equalTo("OK"));
                    } finally {
                        latch.countDown();
                    }
                    return null;
                });
            }

            executorService.invokeAll(tasks);
            latch.await();
            executorService.shutdown();
        }

        @Test
        void deduct_일부_동시_실패() throws InterruptedException {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.CUSTOMER);
            long customerId = findUserByEmail(email).getId();
            Product product = insertProduct("상품", customerId, ProductStatus.ACTIVE, 10);

            ProductDTO.DeductRequest deductRequest = ProductDTO.DeductRequest.builder()
                    .quantity(4)
                    .build();

            ExecutorService executorService = Executors.newFixedThreadPool(3);
            CountDownLatch latch = new CountDownLatch(3);

            List<Callable<Void>> tasks = new ArrayList<>();
            AtomicLong successCount = new AtomicLong();

            for (int i = 0; i < 3; i++) {
                tasks.add(() -> {
                    try {
                        var response = given().contentType(ContentType.JSON)
                                .header("Authorization", "Bearer " + createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.CUSTOMER, email, customerId))
                                .body(deductRequest)
                                .when()
                                .post("/products/deduction/" + product.getId());

                        if (response.statusCode() == 200) successCount.getAndIncrement();
                        return null;
                    } finally {
                        latch.countDown();
                    }
                });
            }

            executorService.invokeAll(tasks);

            latch.await();
            executorService.shutdown();

            Assertions.assertEquals(2, successCount.get());
        }

        @Test
        void deduct_제품_없음_실패() {
            String email = createRandomEmail();
            insertUser("user", email, createRandomPhoneNumber(), "password", UserRole.CUSTOMER);
            long wrongProductId = new Random().nextInt(10000000);

            ProductDTO.DeductRequest deductRequest = ProductDTO.DeductRequest.builder()
                    .quantity(1000)
                    .build();

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + createToken(authProperties.getAccessTokenAlgorithm(), expiration, UserRole.CUSTOMER, email, new Random().nextLong()))
                    .body(deductRequest)
                    .when()
                    .post("/products/deduction/" + wrongProductId)
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("PRODUCT_NOT_FOUND"));
        }
    }
}