package com.huacai.common.exception;

import com.huacai.common.model.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException exception, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return ApiResponse.fail(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletResponse response
    ) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return ApiResponse.fail(joinFieldErrors(exception.getBindingResult().getFieldErrors()));
    }

    @ExceptionHandler(BindException.class)
    public ApiResponse<Void> handleBindException(BindException exception, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return ApiResponse.fail(joinFieldErrors(exception.getBindingResult().getFieldErrors()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Void> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletResponse response
    ) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return ApiResponse.fail(exception.getMessage());
    }

    /**
     * 兜底处理唯一约束冲突（并发场景下 service 层先查后插可能失效）
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResponse<Void> handleDuplicateKeyException(DuplicateKeyException exception, HttpServletResponse response) {
        log.warn("DuplicateKeyException: {}", exception.getMessage());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        String message = parseDuplicateKeyMessage(exception.getMessage());
        return ApiResponse.fail(message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> handleAccessDeniedException(AccessDeniedException exception, HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return ApiResponse.fail("没有访问权限");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception, HttpServletResponse response) {
        log.error("Unhandled exception", exception);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ApiResponse.fail("系统异常，请稍后重试");
    }

    private String joinFieldErrors(java.util.List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .filter(message -> message != null && !message.isBlank())
                .collect(Collectors.joining("；"));
    }

    /**
     * 解析唯一约束异常消息，返回业务化文案
     */
    private String parseDuplicateKeyMessage(String message) {
        if (message == null) {
            return "数据已存在，请检查后重试";
        }
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("uk_cust_customer_customer_no") || lowerMessage.contains("customer_no")) {
            return "客户编号已存在";
        }
        if (lowerMessage.contains("uk_sys_user_username") || lowerMessage.contains("username")) {
            return "用户名已存在";
        }
        if (lowerMessage.contains("uk_sys_role_role_code") || lowerMessage.contains("role_code")) {
            return "角色编码已存在";
        }
        // 无法识别的约束，返回通用提示
        return "数据已存在，请检查后重试";
    }
}
