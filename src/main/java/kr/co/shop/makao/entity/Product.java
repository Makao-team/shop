package kr.co.shop.makao.entity;

import jakarta.persistence.*;
import kr.co.shop.makao.dto.ProductDTO;
import kr.co.shop.makao.entity.base.Auditable;
import kr.co.shop.makao.entity.converter.ProductStatusConverter;
import kr.co.shop.makao.enums.ProductStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@SuperBuilder
@Entity(name = "product")
public class Product extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Convert(converter = ProductStatusConverter.class)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false, name = "merchant_id")
    private long merchantId;

    public void update(ProductDTO.UpdateRequest dto) {
        dto.name().ifPresent((name) -> this.name = name);
        dto.description().ifPresent((description) -> this.description = description);
        dto.price().ifPresent((price) -> this.price = price);
        dto.stock().ifPresent((stock) -> this.stock = stock);
    }

    public boolean isActive() {
        return this.status == ProductStatus.ACTIVE;
    }

    public interface View {
        Long getId();

        String getName();

        String getDescription();

        Integer getPrice();

        Integer getStock();

        String getMerchantName();

        LocalDateTime getCreatedAt();
    }
}
