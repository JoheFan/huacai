package com.huacai.common.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.HealthVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<HealthVO> health() {
        return ApiResponse.success(new HealthVO("UP", "huacai-backend", "0.0.1-SNAPSHOT"));
    }
}
