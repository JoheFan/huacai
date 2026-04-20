package com.huacai.auth.service;

import com.huacai.auth.dto.ChangePasswordRequest;
import com.huacai.auth.dto.LoginRequest;
import com.huacai.auth.vo.CurrentUserInfoVO;
import com.huacai.auth.vo.LoginResponse;
import com.huacai.security.AuthUser;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    CurrentUserInfoVO currentUser();

    void changePassword(ChangePasswordRequest request);

    AuthUser loadAuthUser(Long userId);
}
