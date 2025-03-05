package kr.co.shop.makao.service;

import kr.co.shop.makao.dto.UserDTO;
import kr.co.shop.makao.entity.User;
import kr.co.shop.makao.repository.UserRepository;
import kr.co.shop.makao.response.CommonException;
import kr.co.shop.makao.util.StringEncoder;
import kr.co.shop.makao.vo.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional
    public void save(UserDTO.SaveRequest dto) {
        var exists = userRepository.existsEmailAndPhoneNumber(dto.email(), dto.phoneNumber());
        if (exists.getEmailExists())
            throw CommonException.BAD_REQUEST.toException("EMAIL_DUPLICATED");
        if (exists.getPhoneNumberExists())
            throw CommonException.BAD_REQUEST.toException("PHONE_NUMBER_DUPLICATED");

        var user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .password(StringEncoder.encode(dto.password()))
                .role(dto.role())
                .build();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDTO.SignInResponse signIn(UserDTO.SignInRequest dto) {
        var user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> CommonException.BAD_REQUEST.toException("USER_NOT_FOUND"));

        if (!StringEncoder.match(dto.password(), user.getPassword()))
            throw CommonException.BAD_REQUEST.toException("AUTHENTICATION_FAILED");

        var tokens = authService.issue(AuthUser.builder()
                .email(dto.email())
                .id(user.getId())
                .role(user.getRole().getValue())
                .build());
        return UserDTO.SignInResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .role(user.getRole())
                .build();
    }
}
