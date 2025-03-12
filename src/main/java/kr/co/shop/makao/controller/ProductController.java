package kr.co.shop.makao.controller;

import jakarta.validation.Valid;
import kr.co.shop.makao.dto.ProductDTO;
import kr.co.shop.makao.enums.UserRole;
import kr.co.shop.makao.resolver.Available;
import kr.co.shop.makao.response.CommonResponse;
import kr.co.shop.makao.service.ProductService;
import kr.co.shop.makao.vo.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Validated
@RequestMapping("/products")
@RestController
public class ProductController {
    private final ProductService productService;

    @Available(roles = {UserRole.ADMIN, UserRole.MERCHANT})
    @GetMapping
    ResponseEntity<CommonResponse<ProductDTO.FindAllDetailResponse>> findAll(
            @Valid @ModelAttribute ProductDTO.FindAllDetailRequest dto,
            AuthUser authUser
    ) {
        return CommonResponse.success(productService.findAllDetail(dto, authUser));
    }


    @GetMapping("/view")
    ResponseEntity<CommonResponse<ProductDTO.FindAllViewResponse>> findAllView(
            @Valid @ModelAttribute ProductDTO.FindAllViewRequest dto
    ) {
        return CommonResponse.success(productService.findAllView(dto));
    }

    @GetMapping("/view/{id}")
    ResponseEntity<CommonResponse<ProductDTO.FindOneViewResponse>> findOneView(
            @Valid @ModelAttribute ProductDTO.FindOneViewRequest dto
    ) {
        return CommonResponse.success(productService.findOneView(dto));
    }

    @Available(roles = {UserRole.ADMIN, UserRole.MERCHANT})
    @PostMapping
    ResponseEntity<CommonResponse<Void>> save(
            @Valid @RequestBody ProductDTO.SaveRequest dto,
            AuthUser authUser
    ) {
        productService.save(dto, authUser);
        return CommonResponse.success(null);
    }

    @Available(roles = {UserRole.ADMIN, UserRole.MERCHANT})
    @PatchMapping("/{id}")
    ResponseEntity<CommonResponse<Void>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO.UpdateRequest dto,
            AuthUser authUser
    ) {
        productService.update(id, dto, authUser);
        return CommonResponse.success(null);
    }
}
