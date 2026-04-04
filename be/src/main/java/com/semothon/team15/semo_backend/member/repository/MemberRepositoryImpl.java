package com.semothon.team15.semo_backend.member.repository;

import com.semothon.team15.semo_backend.member.entity.MemberEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final MongoTemplate mongoTemplate;

    public MemberRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<MemberEntity> findMemberByLoginId(String loginId) {
        Query query = new Query(Criteria.where("loginId").is(loginId));
        MemberEntity member = mongoTemplate.findOne(query, MemberEntity.class, "member_info");
        return Optional.ofNullable(member);
    }

    @Override
    public boolean isLoginIdAvailable(String loginId) {
        try {
            Query query = new Query(Criteria.where("loginId").is(loginId));
            MemberEntity member = mongoTemplate.findOne(query, MemberEntity.class, "member_info");
            return member == null;
        } catch (Exception e) {
            System.err.println("Error checking login ID availability: " + e.getMessage());
            return false;
        }
    }

}