package com.hulahoop.bikewayback.model.dto;

public class ReservationDTO {
    private int recordNum;
    private String reservationDateTime;
    private String startTime;
    private String endTime;
    private String paymentTime;
    private String state;
    private int bicycleCode;
    private String bicycleType;
    private int ratePerHour;
    private String durationHours;
    private int totalAmount;
    private String phoneNumber;
    private Long transactionNum; // Admin 서버 거래 번호

    public ReservationDTO() {
    }

    public ReservationDTO(int recordNum, String reservationDateTime, String startTime, String endTime,
            String paymentTime, String state, int bicycleCode, String bicycleType, int ratePerHour,
            String durationHours, int totalAmount, String phoneNumber) {
        this.recordNum = recordNum;
        this.reservationDateTime = reservationDateTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.paymentTime = paymentTime;
        this.state = state;
        this.bicycleCode = bicycleCode;
        this.bicycleType = bicycleType;
        this.ratePerHour = ratePerHour;
        this.durationHours = durationHours;
        this.totalAmount = totalAmount;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(String durationHours) {
        this.durationHours = durationHours;
    }

    public int getRatePerHour() {
        return ratePerHour;
    }

    public void setRatePerHour(int ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public String getBicycleType() {
        return bicycleType;
    }

    public void setBicycleType(String bicycleType) {
        this.bicycleType = bicycleType;
    }

    public int getBicycleCode() {
        return bicycleCode;
    }

    public void setBicycleCode(int bicycleCode) {
        this.bicycleCode = bicycleCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(String reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

    public int getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(int recordNum) {
        this.recordNum = recordNum;
    }

    public Long getTransactionNum() {
        return transactionNum;
    }

    public void setTransactionNum(Long transactionNum) {
        this.transactionNum = transactionNum;
    }

    @Override
    public String toString() {
        return "ReservationDTO{" +
                "recordNum=" + recordNum +
                ", reservationDateTime='" + reservationDateTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", paymentTime='" + paymentTime + '\'' +
                ", state='" + state + '\'' +
                ", bicycleCode=" + bicycleCode +
                ", bicycleType='" + bicycleType + '\'' +
                ", ratePerHour=" + ratePerHour +
                ", durationHours='" + durationHours + '\'' +
                ", totalAmount=" + totalAmount +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", transactionNum=" + transactionNum +
                '}';
    }
}
