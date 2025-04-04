package com.semothon.team15.semo_backend.member.controller;

import com.semothon.team15.semo_backend.common.dto.BaseResponse;
import com.semothon.team15.semo_backend.member.dto.*;
import com.semothon.team15.semo_backend.member.service.EmailService;
import com.semothon.team15.semo_backend.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;

    public MemberController(MemberService memberService, EmailService emailService) {
        this.memberService = memberService;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<String>> signUp(@Valid @RequestBody MemberDto memberDto) {
        String result = memberService.signUp(memberDto);

        if (result.startsWith("이미 등록된") || result.startsWith("필수")) {
            return ResponseEntity.badRequest().body(
                    new BaseResponse<>(
                            "FAIL",
                            null,
                            result
                    )
            );
        }
        return ResponseEntity.ok(
                new BaseResponse<>(
                        "SUCCESS",
                        null,
                        result
                )
        );
    }

    @GetMapping("/check-login-id")
    public ResponseEntity<BaseResponse<Boolean>> checkLoginId(@RequestParam String loginId) {
        boolean isAvailable = memberService.checkLoginIdAvailability(loginId);
        return ResponseEntity.ok(
                new BaseResponse<>(
                        "SUCCESS",
                        isAvailable,
                        isAvailable ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다."
                )
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<BaseResponse<String>> verifyEmail(
            @RequestParam String email,
            @RequestParam String code) {

        Boolean result = emailService.verifyEmailCode(email, code);

        if (!result) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new BaseResponse<>(
                            "FAIL",
                            null,
                            "이메일 인증 실패"
                    )
            );
        }

        return ResponseEntity.ok(
                new BaseResponse<>(
                        "SUCCESS",
                        null,
                        "이메일 인증 성공"
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<String>> login(@RequestBody LoginDto loginDto) {
        String result = memberService.login(loginDto);

        if (result.startsWith("로그인 정보가") || result.startsWith("로그인 아이디와 비밀번호")) {
            return ResponseEntity.badRequest().body((
                    new BaseResponse<>(
                            "FAIL",
                            null,
                            result
                    )
            ));
        }
        return ResponseEntity.ok(
                new BaseResponse<>(
                        "SUCCESS",
                        null,
                        "로그인 성공"
                )
        );
    }
}