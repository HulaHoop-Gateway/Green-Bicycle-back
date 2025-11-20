package com.hulahoop.bikewayback.model.info;

public class BicycleInfo {

    private int bicycleCode;

    private double longitude;

    private double latitude;

    private String bicycleType;

    // 💡 [수정] T_BicycleInfo 테이블의 컬럼과 정확히 매칭되도록 transient와 기본값을 제거
    private String status;

    // 기본 생성자
    public BicycleInfo() {
    }

    // 💡 [수정] DB의 모든 컬럼을 포함하도록 생성자 업데이트
    public BicycleInfo(int bicycleCode, double longitude, double latitude, String bicycleType, String status) {
        this.bicycleCode = bicycleCode;
        this.longitude = longitude;
        this.latitude = latitude;
        this.bicycleType = bicycleType;
        this.status = status; // DB에서 읽어온 실제 상태 값을 설정
    }

    // Getter and Setter

    public int getBicycleCode() {
        return bicycleCode;
    }

    public void setBicycleCode(int bicycleCode) {
        this.bicycleCode = bicycleCode;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getBicycleType() {
        return bicycleType;
    }

    public void setBicycleType(String bicycleType) {
        this.bicycleType = bicycleType;
    }

    // 💡 status 필드는 이제 DB 컬럼 값을 받습니다.
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}