package kr.co.shop.makao.service;

import kr.co.shop.makao.dto.AuthDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private UserService userService;

    @Test
    void signUp_성공() {
        var dto = AuthDTO.SignUpRequest.builder()
                .name("name")
                .email("email")
                .phoneNumber("phoneNumber")
                .password("password")
                .build();

        doNothing().when(userService).save(dto);

        authService.signUp(dto);
    }
}