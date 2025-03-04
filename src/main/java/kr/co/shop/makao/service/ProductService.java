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

    private boolean isMerchant(String role) {
        return role.equals(UserRole.MERCHANT.getValue());
    }
}