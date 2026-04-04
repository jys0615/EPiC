package com.semothon.team15.semo_backend.member.dto;

import com.semothon.team15.semo_backend.common.status.ROLE;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MemberDto {
    @NotBlank
    @JsonProperty("loginId")
    private String loginId;

    @NotBlank
    @JsonProperty("password")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,20}$",
            message = "영문, 숫자, 특수문자를 포함한 8~20자리로 입력해주세요"
    )
    private String password;

    @NotBlank
    @JsonProperty("name")
    private String name;

    @NotBlank
    @Email
    @JsonProperty("email")
    private String email;

//    @ValidEnum(enumClass = ROLE.class, message = "유효하지 않은 ROLE 값입니다.")
//    @JsonProperty("role")
//    private ROLE role;

    // Getters and Setters
    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public ROLE getRole() {
//        return role;
//    }
//
//    public void setRole(ROLE role) {
//        this.role = role;
//    }
}