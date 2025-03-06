package kr.co.shop.makao.service;

import kr.co.shop.makao.dto.ProductDTO;
import kr.co.shop.makao.entity.Product;
import kr.co.shop.makao.enums.ProductStatus;
import kr.co.shop.makao.enums.UserRole;
import kr.co.shop.makao.repository.ProductRepository;
import kr.co.shop.makao.repository.UserRepository;
import kr.co.shop.makao.response.CommonException;
import kr.co.shop.makao.vo.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void save(ProductDTO.SaveRequest dto, AuthUser authUser) {
        var merchant = userRepository.findById(dto.merchantId())
                .orElseThrow(() -> CommonException.BAD_REQUEST.toException("MERCHANT_NOT_FOUND"));

        if (isMerchant(authUser.role()) && merchant.getId() != authUser.id())
            throw CommonException.FORBIDDEN.toException("FORBIDDEN");

        var product = Product.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .stock(dto.stock())
                .status(ProductStatus.PENDING)
                .merchantId(merchant.getId())
                .build();

        productRepository.save(product);
    }

    @Transactional
    public void update(Long id, ProductDTO.UpdateRequest dto, AuthUser authUser) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> CommonException.BAD_REQUEST.toException("PRODUCT_NOT_FOUND"));

        if (isMerchant(authUser.role()) && product.getMerchantId() != authUser.id())
            throw CommonException.FORBIDDEN.toException("FORBIDDEN");

        product.update(dto);
    }

    @Transactional(readOnly = true)
    public ProductDTO.FindAllViewResponse findAllView(ProductDTO.FindAllViewRequest dto) {
        var slice = productRepository.findAllView(
                dto.filter() == null ? null : dto.filter().name(),
                dto.keyword(),
                PageRequest.of(dto.page(), dto.size())
        );

        return ProductDTO.FindAllViewResponse.builder()
                .contents(slice.getContent())
                .last(slice.isLast())
                .build();
    }

    private boolean isMerchant(String role) {
        return role.equals(UserRole.MERCHANT.getValue());
    }
}