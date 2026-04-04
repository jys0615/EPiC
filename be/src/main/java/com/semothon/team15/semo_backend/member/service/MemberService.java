package com.semothon.team15.semo_backend.member.service;

import com.semothon.team15.semo_backend.common.exception.CustomException;
import com.semothon.team15.semo_backend.common.status.ErrorCode;
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
import java.util.Optional;

@Service
public class MemberService {

    private final MongoTemplate memberMongoTemplate;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepositoryImpl memberRepository;

    public MemberService(
            @Qualifier("memberMongoTemplate") MongoTemplate memberMongoTemplate,
            PasswordEncoder passwordEncoder,
            MemberRepositoryImpl memberRepository) {
        this.memberMongoTemplate = memberMongoTemplate;
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
    }

    public Optional<MemberEntity> getMemberByLoginId(String loginId) {
        return memberRepository.findMemberByLoginId(loginId);
    }

    public boolean checkLoginIdAvailability(String loginId) {
        return memberRepository.isLoginIdAvailable(loginId);
    }

    public void signUp(MemberDto memberDto) {
        if (!checkLoginIdAvailability(memberDto.getLoginId())) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID, "이미 등록된 아이디입니다.");
        }

        MemberEntity member = new MemberEntity(
                null,
                memberDto.getLoginId(),
                passwordEncoder.encode(memberDto.getPassword()),
                memberDto.getName(),
                memberDto.getEmail(),
                ROLE.MEMBER);

        memberMongoTemplate.save(member, "member_info");
    }

    public void login(LoginDto loginDto) {
        Query query = new Query(Criteria.where("loginId").is(loginDto.getLoginId()));
        MemberEntity member = memberMongoTemplate.findOne(query, MemberEntity.class, "member_info");

        if (member == null || !passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "로그인 정보가 일치하지 않습니다.");
        }
    }
}
