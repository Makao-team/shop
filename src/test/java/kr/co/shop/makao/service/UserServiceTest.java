package kr.co.shop.makao.service;

import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.enums.UserRole;
import kr.co.shop.makao.repository.ExistsEmailAndPhoneNumber;
import kr.co.shop.makao.repository.UserRepository;
import kr.co.shop.makao.response.CommonExceptionImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ExistsEmailAndPhoneNumber existsEmailAndPhoneNumber;

    @Nested
    class save {
        AuthDTO.SignUpRequest dto = AuthDTO.SignUpRequest.builder()
                .name("name")
                .email("email")
                .phoneNumber("phoneNumber")
                .password("password")
                .role(UserRole.CUSTOMER)
                .build();

        @Test
        void save_성공() {
            when(existsEmailAndPhoneNumber.getEmailExists()).thenReturn(false);
            when(existsEmailAndPhoneNumber.getPhoneNumberExists()).thenReturn(false);
            when(userRepository.existsEmailAndPhoneNumber(anyString(), anyString())).thenReturn(existsEmailAndPhoneNumber);

            userService.save(dto);
        }

        @Test
        void save_이메일_중복_실패() {
            when(existsEmailAndPhoneNumber.getEmailExists()).thenReturn(true);
            when(userRepository.existsEmailAndPhoneNumber(anyString(), anyString())).thenReturn(existsEmailAndPhoneNumber);

            var exception = assertThrows(CommonExceptionImpl.class, () -> userService.save(dto));
            assert exception.getMessage().equals("EMAIL_DUPLICATED");
        }

        @Test
        void save_전화번호_중복_실패() {
            when(existsEmailAndPhoneNumber.getEmailExists()).thenReturn(false);
            when(existsEmailAndPhoneNumber.getPhoneNumberExists()).thenReturn(true);
            when(userRepository.existsEmailAndPhoneNumber(anyString(), anyString())).thenReturn(existsEmailAndPhoneNumber);

            var exception = assertThrows(CommonExceptionImpl.class, () -> userService.save(dto));
            assert exception.getMessage().equals("PHONE_NUMBER_DUPLICATED");
        }
    }
}