package kr.co.shop.makao.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.co.shop.makao.enums.UserRole;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {
    @Override
    public String convertToDatabaseColumn(UserRole role) {
        return role == null ? null : role.getValue();
    }

    @Override
    public UserRole convertToEntityAttribute(String value) {
        return value == null ? null : UserRole.fromValue(value);
    }
}
