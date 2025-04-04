package com.semothon.team15.semo_backend.member.service;

//import com.semothon.team15.semo_backend.common.authority.JwtTokenProvider;
import com.semothon.team15.semo_backend.common.exception.InvalidInputException;
import com.semothon.team15.semo_backend.common.status.ROLE;
import com.semothon.team15.semo_backend.member.dto.*;
import com.semothon.team15.semo_backend.member.entity.MemberEntity;
import com.semothon.team15.semo_backend.member.repository.MemberRepositoryImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MongoTemplate memberMongoTemplate;
    //private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepositoryImpl memberRepository;

    public MemberService(
            @Qualifier("memberMongoTemplate") MongoTemplate memberMongoTemplate,
            //JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder,
            MemberRepositoryImpl memberRepository) {
        this.memberMongoTemplate = memberMongoTemplate;
        //this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
    }

    public Optional<MemberEntity> getMemberByLoginId(String loginId) {
        return memberRepository.findMemberByLoginId(loginId);
    }

    public boolean checkLoginIdAvailability(String loginId) {
        return memberRepository.isLoginIdAvailable(loginId);
    }

    public String signUp(MemberDto memberDto) {
        if (memberDto.getLoginId().isBlank() ||
                memberDto.getPassword().isBlank() ||
                memberDto.getName().isBlank() ||
                memberDto.getEmail().isBlank()) {
            return "필수 입력값이 누락되었습니다.";
        }

        if (!checkLoginIdAvailability(memberDto.getLoginId())) {
            return "이미 등록된 아이디입니다.";
        }

        MemberEntity member = new MemberEntity(
                null,
                memberDto.getLoginId(),
                passwordEncoder.encode(memberDto.getPassword()),
                memberDto.getName(),
                memberDto.getEmail(),
                ROLE.MEMBER
        );

        memberMongoTemplate.save(member, "member_info");
        return "회원가입이 완료되었습니다.";
    }

    public String login(LoginDto loginDto) {
        if (loginDto.getLoginId().isBlank() || loginDto.getPassword().isBlank()) {
            return "로그인 아이디와 비밀번호를 입력해주세요.";
        }

        Query query = new Query(Criteria.where("loginId").is(loginDto.getLoginId()));
        MemberEntity member = memberMongoTemplate.findOne(query, MemberEntity.class, "member_info");

        System.out.println(member);
        if (member == null || !passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            return "로그인 정보가 일치하지 않습니다.";
        }
        return "로그인 성공";

        //return jwtTokenProvider.generateToken(member.getLoginId(), List.of(member.getRole().name()));
    }

//    public void updateLoginId(String username, LoginIdUpdateRequest request) {
//        Member member = memberRepository.findMemberByLoginId(username)
//                .orElseThrow(() -> new InvalidInputException("loginId", "회원 정보를 찾을 수 없습니다."));
//
//        if (!checkLoginIdAvailability(request.getNewLoginId())) {
//            throw new InvalidInputException("loginId", "이미 사용 중인 아이디입니다.");
//        }
//
//        member.setLoginId(request.getNewLoginId());
//        memberMongoTemplate.save(member, "member_info");
//    }

//    public void updatePassword(String username, PasswordUpdateRequest request) {
//        Member member = memberRepository.findMemberByLoginId(username)
//                .orElseThrow(() -> new InvalidInputException("loginId", "회원 정보를 찾을 수 없습니다."));
//
//        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
//            throw new InvalidInputException("currentPassword", "현재 비밀번호가 일치하지 않습니다.");
//        }
//
//        String pattern = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,20}$";
//        if (!request.getNewPassword().matches(pattern)) {
//            throw new InvalidInputException(
//                    "newPassword",
//                    "영문, 숫자, 특수문자를 포함한 8~20자리 비밀번호를 입력해주세요."
//            );
//        }
//
//        member.setPassword(passwordEncoder.encode(request.getNewPassword()));
//        memberMongoTemplate.save(member, "member_info");
//    }
}
