package kr.co.shop.makao.controller;

import jakarta.validation.Valid;
import kr.co.shop.makao.dto.UserDTO;
import kr.co.shop.makao.response.CommonResponse;
import kr.co.shop.makao.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/")
    public ResponseEntity<CommonResponse<Void>> save(@RequestBody @Valid UserDTO.SaveRequest dto) {
        userService.save(dto);
        return CommonResponse.success(null);
    }
}
