package kr.co.shop.makao.service;

import jakarta.transaction.Transactional;
import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.entity.User;
import kr.co.shop.makao.repository.UserRepository;
import kr.co.shop.makao.response.CommonException;
import kr.co.shop.makao.util.StringEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void save(AuthDTO.SignUpRequest dto) {
        var exists = userRepository.existsEmailAndPhoneNumber(dto.email(), dto.phoneNumber());
        if (exists.getEmailExists())
            throw CommonException.BAD_REQUEST.toException("EMAIL_DUPLICATED");
        if (exists.getPhoneNumberExists())
            throw CommonException.BAD_REQUEST.toException("PHONE_NUMBER_DUPLICATED");

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .password(StringEncoder.encode(dto.password()))
                .role(dto.role())
                .build();

        userRepository.save(user);
    }
}
