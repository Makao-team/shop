package kr.co.shop.makao.entity;

import jakarta.persistence.*;
import kr.co.shop.makao.entity.base.Auditable;
import kr.co.shop.makao.entity.converter.ProductStatusConverter;
import kr.co.shop.makao.enums.ProductStatus;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
}
