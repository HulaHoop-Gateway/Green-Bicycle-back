package com.hulahoop.bikewayback.model.dao;

import com.hulahoop.bikewayback.model.dto.MemberLoginDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {
    MemberLoginDTO login(@Param("id") String id, @Param("password") String password);

    // ✅ 전화번호로 회원 코드 조회
    Integer findMemberCodeByPhone(@Param("phone") String phone);
}