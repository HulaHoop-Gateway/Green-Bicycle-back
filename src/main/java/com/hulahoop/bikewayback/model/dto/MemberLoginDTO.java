package com.hulahoop.bikewayback.model.dto;

public class MemberLoginDTO {
    private String id;
    private String password;
    private Integer memberCode;

    public MemberLoginDTO() {
    }

    public MemberLoginDTO(Integer memberCode, String password, String id) {
        this.memberCode = memberCode;
        this.password = password;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(Integer memberCode) {
        this.memberCode = memberCode;
    }

    @Override
    public String toString() {
        return "MemberLoginDTO{" +
                "id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", memberCode=" + memberCode +
                '}';
    }
}