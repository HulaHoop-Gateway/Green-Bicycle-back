package com.hulahoop.bikewayback.controller;

import com.hulahoop.bikewayback.model.dto.MemberLoginDTO;
import com.hulahoop.bikewayback.model.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginDTO loginDTO) {
        System.out.println("로그인 요청: " + loginDTO);

        MemberLoginDTO result = memberService.login(loginDTO.getId(), loginDTO.getPassword());

        System.out.println("로그인 결과: " + result);

        if (result != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그인 성공");
            response.put("id", result.getId());
            response.put("member_code", result.getMemberCode()); // ✅ 추가
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "로그인 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}