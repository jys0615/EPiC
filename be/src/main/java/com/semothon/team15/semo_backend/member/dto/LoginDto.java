package com.semothon.team15.semo_backend.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class LoginDto {

    @NotBlank
    @JsonProperty("loginId")
    private final String _loginId;

    @NotBlank
    @JsonProperty("password")
    private final String _password;

    public LoginDto(@JsonProperty("loginId") String loginId,
                    @JsonProperty("password") String password) {
        this._loginId = loginId;
        this._password = password;
    }

    public String getLoginId() {
        if (_loginId == null) {
            throw new IllegalStateException("loginId cannot be null");
        }
        return _loginId;
    }

    public String getPassword() {
        if (_password == null) {
            throw new IllegalStateException("password cannot be null");
        }
        return _password;
    }
}
