package com.hulahoop.bikewayback.model.dto;

public class BicycleResponseDTO {

    private int bicycleCode;
    private double latitude;
    private double longitude;
    private String bicycleType;
    private String status;
    private double distanceKm;
    private String label;

    public BicycleResponseDTO() {}

    public BicycleResponseDTO(int bicycleCode, double latitude, double longitude,
                              String bicycleType, String status, double distanceKm) {
        this.bicycleCode = bicycleCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bicycleType = bicycleType;
        this.status = status;
        this.distanceKm = distanceKm;
        this.label = formatLabel();
    }

    public int getBicycleCode() { return bicycleCode; }
    public void setBicycleCode(int bicycleCode) { this.bicycleCode = bicycleCode; updateLabel(); }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getBicycleType() { return bicycleType; }
    public void setBicycleType(String bicycleType) { this.bicycleType = bicycleType; updateLabel(); }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; updateLabel(); }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    /** ✅ 추가: Gemini가 name 필드를 찾을 수 있도록 대응 */
    public String getName() { return label; }

    private String formatLabel() {
        return bicycleCode + "-" + (bicycleType != null ? bicycleType : "") + "-" + (status != null ? status : "");
    }

    private void updateLabel() {
        this.label = formatLabel();
    }
}
