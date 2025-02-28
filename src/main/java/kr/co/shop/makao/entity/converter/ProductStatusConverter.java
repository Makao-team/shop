package kr.co.shop.makao.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.co.shop.makao.enums.ProductStatus;

@Converter(autoApply = true)
public class ProductStatusConverter implements AttributeConverter<ProductStatus, String> {
    @Override
    public String convertToDatabaseColumn(ProductStatus status) {
        return status == null ? null : status.getValue();
    }

    @Override
    public ProductStatus convertToEntityAttribute(String value) {
        return value == null ? null : ProductStatus.fromValue(value);
    }
}
