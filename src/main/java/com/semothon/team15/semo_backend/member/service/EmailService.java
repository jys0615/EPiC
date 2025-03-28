package com.semothon.team15.semo_backend.member.service;

import com.semothon.team15.semo_backend.common.status.ErrorCode;
import com.semothon.team15.semo_backend.common.status.UserSignUpResponseCode;
import com.semothon.team15.semo_backend.member.dto.EmailDtoResponse;
import com.semothon.team15.semo_backend.common.exception.CustomException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final RedisService redisService;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender emailSender, TemplateEngine templateEngine,
                        RedisService redisService) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.redisService = redisService;
    }

    @Async
    public CompletableFuture<EmailDtoResponse<Void>> sendAuthEmail(String email, String authCode) {
        try {
            MimeMessage emailForm = makeEmailForm(email, authCode);

            if (redisService.existData(email)) {
                redisService.deleteData(email);
            }

            emailSender.send(emailForm);
            redisService.setDataExpire(email, authCode, 3 * 60 * 1000L);

            return CompletableFuture.completedFuture(
                    new EmailDtoResponse<>(
                            null,
                            HttpStatus.OK,
                            true,
                            UserSignUpResponseCode.SUCCESS.getMessage(),
                            null
                    )
            );
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST,
                    e.getMessage() != null ? e.getMessage() : "이메일 전송 중 오류 발생");
        }
    }

    private MimeMessage makeEmailForm(String email, String authCode) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("회원가입 이메일 인증");
        message.setFrom(from);
        message.setContent(setContext(authCode), "text/html;charset=euc-kr");
        return message;
    }

    private String setContext(String authCode) {
        Context context = new Context();
        context.setVariable("code", authCode);
        return templateEngine.process("email", context);
    }

    public String makeAuthCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int type = random.nextInt(3);
            switch (type) {
                case 0: key.append((char) (random.nextInt(26) + 97)); break;
                case 1: key.append((char) (random.nextInt(26) + 65)); break;
                case 2: key.append(random.nextInt(10)); break;
            }
        }
        return key.toString();
    }

    public Boolean verifyEmailCode(String email, String code) {
        String userMailCode = redisService.getData(email);
        return code.equals(userMailCode);
    }

    public EmailDtoResponse<Void> verifyEmailCode(HttpServletRequest request, String email, String code) {
        String userMailCode = redisService.getData(email);

        if (userMailCode == null) {
            return new EmailDtoResponse<>(
                    request,
                    HttpStatus.BAD_REQUEST,
                    false,
                    UserSignUpResponseCode.EXPIRED_AUTH_MAIL_CODE.getMessage(),
                    null
            );
        } else if (userMailCode.equals(code)) {
            return new EmailDtoResponse<>(
                    request,
                    HttpStatus.OK,
                    true,
                    UserSignUpResponseCode.SUCCESS.getMessage(),
                    null
            );
        } else {
            return new EmailDtoResponse<>(
                    request,
                    HttpStatus.UNAUTHORIZED,
                    false,
                    UserSignUpResponseCode.INVALID_AUTH_MAIL_CODE.getMessage(),
                    null
            );
        }
    }

    public static class Pair<A, B> {
        public final A first;
        public final B second;
        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }
}
