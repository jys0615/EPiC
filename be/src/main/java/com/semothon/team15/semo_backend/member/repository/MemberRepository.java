package com.semothon.team15.semo_backend.member.repository;

import com.semothon.team15.semo_backend.member.entity.MemberEntity;
import java.util.Optional;

public interface MemberRepository {
    Optional<MemberEntity> findMemberByLoginId(String loginId);
    boolean isLoginIdAvailable(String loginId);
}
