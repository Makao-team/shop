package kr.co.shop.makao.service;

import kr.co.shop.makao.dto.ProductDTO;
import kr.co.shop.makao.entity.Product;
import kr.co.shop.makao.entity.User;
import kr.co.shop.makao.enums.ProductStatus;
import kr.co.shop.makao.enums.UserRole;
import kr.co.shop.makao.repository.ProductRepository;
import kr.co.shop.makao.repository.UserRepository;
import kr.co.shop.makao.response.CommonExceptionImpl;
import kr.co.shop.makao.vo.AuthUser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;

    @Nested
    class save {
        ProductDTO.SaveRequest dto = ProductDTO.SaveRequest.builder()
                .name("name")
                .description("description")
                .price(1000)
                .stock(10)
                .merchantId(1L)
                .build();
        AuthUser authUser = AuthUser.builder().id(1L).email("email").role(UserRole.MERCHANT.getValue()).build();

        @Test
        void save_성공() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).email("email").build()));
            when(productRepository.save(any(Product.class))).thenReturn(Product.builder().id(1L).build());

            productService.save(dto, authUser);
        }

        @Test
        void save_merchant_없음_실패() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.save(dto, authUser));
            assertThat(exception.getMessage()).isEqualTo("MERCHANT_NOT_FOUND");
        }

        @Test
        void save_권한_없음_실패() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().id(1000L).build()));

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.save(dto, authUser));
            assertThat(exception.getMessage()).isEqualTo("FORBIDDEN");
        }
    }

    @Nested
    class update {
        ProductDTO.UpdateRequest dto = ProductDTO.UpdateRequest.builder()
                .name(Optional.of("name"))
                .description(Optional.of("description"))
                .price(Optional.of(1000))
                .stock(Optional.of(10))
                .status(Optional.of(ProductStatus.PENDING))
                .build();
        AuthUser authUser = AuthUser.builder().id(1L).email("email").role(UserRole.MERCHANT.getValue()).build();

        @Test
        void update_성공() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).merchantId(1L).build()));

            productService.update(1L, dto, authUser);
        }

        @Test
        void update_상품_없음_실패() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.update(1L, dto, authUser));
            assertThat(exception.getMessage()).isEqualTo("PRODUCT_NOT_FOUND");
        }

        @Test
        void update_권한_없음_실패() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).merchantId(1000L).build()));

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.update(1L, dto, authUser));
            assertThat(exception.getMessage()).isEqualTo("FORBIDDEN");
        }
    }
}