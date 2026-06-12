package com.huacai.assistant.controller;

import com.huacai.assistant.dto.AssistantActionConfirmRequest;
import com.huacai.assistant.service.OpportunityAssistantService;
import com.huacai.assistant.vo.AssistantWriteReceiptVO;
import com.huacai.common.model.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "飞书商机助手")
@RestController
@RequestMapping("/api/v1/assistant/actions")
public class AssistantActionController {

    private final OpportunityAssistantService opportunityAssistantService;

    public AssistantActionController(OpportunityAssistantService opportunityAssistantService) {
        this.opportunityAssistantService = opportunityAssistantService;
    }

    @PostMapping("/confirm")
    public ApiResponse<AssistantWriteReceiptVO> confirm(@Valid @RequestBody AssistantActionConfirmRequest request) {
        return ApiResponse.success(opportunityAssistantService.confirmAction(request));
    }
}
