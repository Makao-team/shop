package kr.co.shop.makao.service;

import kr.co.shop.makao.dto.AuthDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthService {
    private final UserService userService;

    public void signUp(AuthDTO.SignUpRequest dto) {
        userService.save(dto);
    }
}
