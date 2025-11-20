package com.hulahoop.bikewayback.model.service;

import com.hulahoop.bikewayback.model.dao.MemberMapper;
import com.hulahoop.bikewayback.model.dto.MemberLoginDTO;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberMapper MemberMapper;

    public MemberService(MemberMapper MemberMapper) {
        this.MemberMapper = MemberMapper;
    }

    public MemberLoginDTO login(String id, String password) {

        return MemberMapper.login(id, password);
    }
}