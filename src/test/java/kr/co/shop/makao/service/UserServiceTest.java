package kr.co.shop.makao.service;

import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.dto.UserDTO;
import kr.co.shop.makao.entity.User;
import kr.co.shop.makao.enums.UserRole;
import kr.co.shop.makao.repository.ExistsEmailAndPhoneNumber;
import kr.co.shop.makao.repository.UserRepository;
import kr.co.shop.makao.response.CommonExceptionImpl;
import kr.co.shop.makao.util.StringEncoder;
import kr.co.shop.makao.vo.AuthUser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ExistsEmailAndPhoneNumber existsEmailAndPhoneNumber;
    @Mock
    private AuthService authService;

    @Nested
    class save {
        UserDTO.SaveRequest dto = UserDTO.SaveRequest.builder()
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

    @Nested
    class SignIn {
        UserDTO.SignInRequest dto = UserDTO.SignInRequest.builder()
                .email("email")
                .password("password123!")
                .build();
        User user = User.builder().password("hashed").role(UserRole.CUSTOMER).build();
        AuthDTO.TokenIssueResponse tokens = AuthDTO.TokenIssueResponse.builder().accessToken("accessToken").refreshToken("refreshToken").build();

        @Test
        void signIn_성공() {
            when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(user));
            try (var stringEncoderMockedStatic = mockStatic(StringEncoder.class)) {
                stringEncoderMockedStatic.when(() -> StringEncoder.match(dto.password(), user.getPassword())).thenReturn(true);

                AuthUser payload = AuthUser.builder().email(dto.email()).role(user.getRole().getValue()).build();
                when(authService.issue(payload)).thenReturn(tokens);

                assertThat(userService.signIn(dto).role()).isEqualTo(user.getRole());
                assertThat(userService.signIn(dto).accessToken()).isEqualTo(tokens.accessToken());
                assertThat(userService.signIn(dto).refreshToken()).isEqualTo(tokens.refreshToken());
            }
        }

        @Test
        void signIn_이메일_없음_실패() {
            when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
            var exception = assertThrows(CommonExceptionImpl.class, () -> userService.signIn(dto));
            assertThat(exception.getMessage()).isEqualTo("USER_NOT_FOUND");
        }

        @Test
        void signIn_패스워드_불일치_실패() {
            when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(user));
            try (var stringEncoderMockedStatic = mockStatic(StringEncoder.class)) {
                stringEncoderMockedStatic.when(() -> StringEncoder.match(dto.password(), user.getPassword())).thenReturn(false);
                var exception = assertThrows(CommonExceptionImpl.class, () -> userService.signIn(dto));
                assertThat(exception.getMessage()).isEqualTo("AUTHENTICATION_FAILED");
            }
        }
    }
}