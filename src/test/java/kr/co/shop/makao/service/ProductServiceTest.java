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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
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
    class updateStatus {
        ProductDTO.UpdateStatusRequest dto = ProductDTO.UpdateStatusRequest.builder()
                .status(ProductStatus.ACTIVE)
                .build();
        AuthUser authUser = AuthUser.builder().id(1L).email("email").role(UserRole.MERCHANT.getValue()).build();

        @Test
        void updateStatus_성공() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).merchantId(1L).build()));

            productService.updateStatus(1L, dto, authUser);
        }

        @Test
        void updateStatus_상품_없음_실패() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.updateStatus(1L, dto, authUser));
            assertThat(exception.getMessage()).isEqualTo("PRODUCT_NOT_FOUND");
        }

        @Test
        void updateStatus_권한_없음_실패() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).merchantId(1000L).build()));

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.updateStatus(1L, dto, authUser));
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
        void update_상품_활성화_실패() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).merchantId(1L).status(ProductStatus.ACTIVE).build()));

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.update(1L, dto, authUser));
            assertThat(exception.getMessage()).isEqualTo("PRODUCT_MUST_BE_PENDING");
        }

        @Test
        void update_권한_없음_실패() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).merchantId(1000L).build()));

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.update(1L, dto, authUser));
            assertThat(exception.getMessage()).isEqualTo("FORBIDDEN");
        }
    }

    @Nested
    class findAllDetail {
        PageRequest pageRequest = PageRequest.of(0, 10);
        AuthUser authUser = AuthUser.builder().id(1L).email("email").role(UserRole.MERCHANT.getValue()).build();

        @Test
        void findAllDetail_filter_null_성공() {
            var dto = ProductDTO.FindAllDetailRequest.builder()
                    .merchantId(1L)
                    .filter(null)
                    .keyword("")
                    .page(0)
                    .size(10)
                    .build();
            var sliceImpl = new SliceImpl<Product>(List.of(), pageRequest, false);

            when(productRepository.findAll(1L, null, "", pageRequest)).thenReturn(sliceImpl);

            var response = productService.findAllDetail(dto, authUser);

            assertThat(response.contents()).isEqualTo(List.of());
        }

        @Test
        void findAllDetail_성공() {
            var dto = ProductDTO.FindAllDetailRequest.builder()
                    .merchantId(1L)
                    .filter(ProductDTO.FindAllDetailRequest.Filter.name)
                    .keyword("keyword")
                    .page(0)
                    .size(10)
                    .build();
            var sliceImpl = new SliceImpl<Product>(List.of(), pageRequest, false);

            when(productRepository.findAll(1L, "name", "keyword", pageRequest)).thenReturn(sliceImpl);

            var response = productService.findAllDetail(dto, authUser);

            assertThat(response.contents()).isEqualTo(List.of());
        }
    }

    @Nested
    class findOneDetail {
        ProductDTO.FindOneRequest dto = ProductDTO.FindOneRequest.builder().id(1L).build();
        AuthUser authUser = AuthUser.builder().id(1L).email("email").role(UserRole.MERCHANT.getValue()).build();

        @Test
        void findOneDetail_성공() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).merchantId(1L).build()));

            var response = productService.findOneDetail(dto, authUser);

            assertThat(response.content()).isNotNull();
        }

        @Test
        void findOneDetail_상품_없음_실패() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.findOneDetail(dto, authUser));
            assertThat(exception.getMessage()).isEqualTo("PRODUCT_NOT_FOUND");
        }

        @Test
        void findOneDetail_권한_없음_실패() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).merchantId(1000L).build()));

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.findOneDetail(dto, authUser));
            assertThat(exception.getMessage()).isEqualTo("FORBIDDEN");
        }
    }

    @Nested
    class findAllView {
        PageRequest pageRequest = PageRequest.of(0, 10);

        @Test
        void findAllView_filter_null_성공() {
            var dto = ProductDTO.FindAllViewRequest.builder()
                    .filter(null)
                    .keyword("")
                    .page(0)
                    .size(10)
                    .build();
            var sliceImpl = new SliceImpl<Product.View>(List.of(), pageRequest, false);

            when(productRepository.findAllView(null, "", pageRequest)).thenReturn(sliceImpl);

            var response = productService.findAllView(dto);

            assertThat(response.contents()).isEqualTo(List.of());
        }

        @Test
        void findAllView_성공() {
            var dto = ProductDTO.FindAllViewRequest.builder()
                    .filter(ProductDTO.FindAllViewRequest.Filter.name)
                    .keyword("keyword")
                    .page(0)
                    .size(10)
                    .build();
            var sliceImpl = new SliceImpl<Product.View>(List.of(), pageRequest, false);

            when(productRepository.findAllView("name", "keyword", pageRequest)).thenReturn(sliceImpl);

            var response = productService.findAllView(dto);

            assertThat(response.contents()).isEqualTo(List.of());
        }
    }

    @Nested
    class findOneView {
        ProductDTO.FindOneRequest dto = ProductDTO.FindOneRequest.builder().id(1L).build();

        @Test
        void findOneView_성공() {
            when(productRepository.findOneView(anyLong())).thenReturn(Optional.of(mock(Product.View.class)));

            var response = productService.findOneView(dto);

            assertThat(response.content()).isNotNull();
        }

        @Test
        void findOneView_상품_없음_실패() {
            when(productRepository.findOneView(anyLong())).thenReturn(Optional.empty());

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.findOneView(dto));
            assertThat(exception.getMessage()).isEqualTo("PRODUCT_NOT_FOUND");
        }
    }

    @Nested
    class archive {
        AuthUser authUser = AuthUser.builder().id(1L).email("email").role(UserRole.MERCHANT.getValue()).build();

        @Test
        void archive_성공() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).merchantId(1L).build()));

            productService.archive(1L, authUser);
        }

        @Test
        void archive_상품_없음_실패() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.archive(1L, authUser));
            assertThat(exception.getMessage()).isEqualTo("PRODUCT_NOT_FOUND");
        }

        @Test
        void archive_상품_활성화_실패() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).merchantId(1L).status(ProductStatus.ACTIVE).build()));

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.archive(1L, authUser));
            assertThat(exception.getMessage()).isEqualTo("PRODUCT_MUST_BE_PENDING");
        }

        @Test
        void archive_권한_없음_실패() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).merchantId(1000L).build()));

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.archive(1L, authUser));
            assertThat(exception.getMessage()).isEqualTo("FORBIDDEN");
        }
    }

    @Nested
    class deduct {
        ProductDTO.DeductRequest dto = ProductDTO.DeductRequest.builder()
                .quantity(1)
                .build();

        @Test
        void deduct_성공() {
            when(productRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).stock(10).build()));

            productService.deduct(1L, dto);
        }

        @Test
        void deduct_상품_없음_실패() {
            when(productRepository.findByIdWithLock(anyLong())).thenReturn(Optional.empty());

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.deduct(1L, dto));
            assertThat(exception.getMessage()).isEqualTo("PRODUCT_NOT_FOUND");
        }

        @Test
        void deduct_재고_부족_실패() {
            when(productRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).stock(0).build()));

            var exception = assertThrows(CommonExceptionImpl.class, () -> productService.deduct(1L, dto));
            assertThat(exception.getMessage()).isEqualTo("PRODUCT_STOCK_NOT_ENOUGH");
        }
    }
}