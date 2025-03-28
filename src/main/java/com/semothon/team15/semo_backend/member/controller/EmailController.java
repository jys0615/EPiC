package com.semothon.team15.semo_backend.member.controller;

import com.semothon.team15.semo_backend.common.dto.BaseResponse;
import com.semothon.team15.semo_backend.member.dto.EmailDtoRequest;
import com.semothon.team15.semo_backend.member.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<BaseResponse<Void>> confirmEmailAuth(@RequestBody EmailDtoRequest dto) {
        String authCode = emailService.makeAuthCode();
        emailService.sendAuthEmail(dto.getEmail(), authCode);

        return ResponseEntity.ok(
                new BaseResponse<>(
                        "SUCCESS",
                        null,
                        "이메일 인증 코드가 전송되었습니다."
                )
        );
    }

    @GetMapping("/auth")
    public ResponseEntity<BaseResponse<Void>> checkMailCode(
            HttpServletRequest request,
            @RequestParam("receiver") String receiver,
            @RequestParam("code") String code) {

        boolean isValid = emailService.verifyEmailCode(request, receiver, code).isSuccess();

        if (isValid) {
            return ResponseEntity.ok(
                    new BaseResponse<>(
                            "SUCCESS",
                            null,
                            "이메일 인증이 완료되었습니다."
                    )
            );
        } else {
            return ResponseEntity.badRequest().body(
                    new BaseResponse<>(
                            "FAIL",
                            null,
                            "잘못된 인증 코드입니다."
                    )
            );
        }
    }
}
