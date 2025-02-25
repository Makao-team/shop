package kr.co.shop.makao.service;

import kr.co.shop.makao.component.AuthTokenManager;
import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.entity.User;
import kr.co.shop.makao.enums.TokenType;
import kr.co.shop.makao.response.CommonException;
import kr.co.shop.makao.util.StringEncoder;
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
        User user = userService.findByEmail(dto.email());

        if (!StringEncoder.match(dto.password(), user.getPassword()))
            throw CommonException.BAD_REQUEST.toException("AUTHENTICATION_FAILED");

        String accessToken = authTokenManager.create(dto.email(), TokenType.ACCESS_TOKEN);
        String refreshToken = authTokenManager.create(dto.email(), TokenType.REFRESH_TOKEN);

        return AuthDTO.SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole())
                .build();
    }

    public AuthDTO.TokenReissueResponse reissue(AuthDTO.TokenReissueRequest dto) {
        String email = authTokenManager.getSubject(dto.refreshToken(), TokenType.REFRESH_TOKEN);
        String accessToken = authTokenManager.create(email, TokenType.ACCESS_TOKEN);
        String refreshToken = authTokenManager.create(email, TokenType.REFRESH_TOKEN);

        return AuthDTO.TokenReissueResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
