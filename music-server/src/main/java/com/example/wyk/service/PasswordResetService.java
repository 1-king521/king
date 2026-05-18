package com.example.wyk.service;

import com.example.wyk.common.R;
import com.example.wyk.model.request.PasswordResetRequest;

public interface PasswordResetService {

    R sendVerificationCode(String email);

    R resetPassword(PasswordResetRequest request);
}
