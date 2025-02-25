package kr.co.shop.makao.controller;

import jakarta.validation.Valid;
import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.response.CommonResponse;
import kr.co.shop.makao.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-in")
    ResponseEntity<CommonResponse<AuthDTO.SignInResponse>> signIn(@RequestBody @Valid AuthDTO.SignInRequest dto) {
        return CommonResponse.success(authService.signIn(dto));
    }

    @PostMapping("/token/reissue")
    ResponseEntity<CommonResponse<AuthDTO.TokenReissueResponse>> reissue(@RequestBody @Valid AuthDTO.TokenReissueRequest dto) {
        return CommonResponse.success(authService.reissue(dto));
    }
}
