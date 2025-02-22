package kr.co.shop.makao.service;

import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.response.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserService userService;
    private final AuthTokenManager authTokenManager;

    @Transactional
    public void signUp(AuthDTO.SignUpRequest dto) {
        userService.save(dto);
    }

    @Transactional(readOnly = true)
    public AuthDTO.SignInResponse signIn(AuthDTO.SignInRequest dto) {
        if (!userService.validateUser(dto.email(), dto.password()))
            throw CommonException.BAD_REQUEST.toException("AUTHENTICATION_FAILED");

        String accessToken = authTokenManager.createAccessToken(dto.email());
        String refreshToken = authTokenManager.createRefreshToken(dto.email());

        return AuthDTO.SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
