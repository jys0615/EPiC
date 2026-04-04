package com.semothon.team15.semo_backend.member.entity;

import com.semothon.team15.semo_backend.common.status.ROLE;
import com.semothon.team15.semo_backend.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "member_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberEntity {

    @MongoId(FieldType.OBJECT_ID)
    private String id;

    @Indexed(unique = true)
    @Field("loginId")
    private String loginId;

    @Field("password")
    private String password;

    @Field("name")
    private String name;

    @Field("email")
    private String email;

    @Field("role")
    private ROLE role;
}