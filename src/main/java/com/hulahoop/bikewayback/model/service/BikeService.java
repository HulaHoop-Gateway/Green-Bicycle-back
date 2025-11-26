package com.hulahoop.bikewayback.model.service;

import com.hulahoop.bikewayback.model.dto.BicycleResponseDTO;
import com.hulahoop.bikewayback.model.dao.BicycleMapper;
import com.hulahoop.bikewayback.model.dao.MemberMapper;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 🚲 BikeService
 * - 위치 기반 자전거 조회 및 상태 관리 비즈니스 로직 담당
 */
@Service
public class BikeService {

    private final BicycleMapper bicycleMapper;
    private final MemberMapper memberMapper;

    public BikeService(BicycleMapper bicycleMapper, MemberMapper memberMapper) {
        this.bicycleMapper = bicycleMapper;
        this.memberMapper = memberMapper;
    }

    // ✅ 가용 자전거 조회 (필터 적용)
    public List<BicycleResponseDTO> findAvailableBikesByLocation(
            double centerLat, double centerLon, double radiusKm, String typeFilter) {
        return bicycleMapper.findAvailableBicyclesByLocation(centerLat, centerLon, radiusKm, typeFilter);
    }

    // ✅ 자전거 상태 변경
    public void updateBicycleStatus(int bicycleCode, String newStatus) {
        bicycleMapper.updateBicycleStatus(bicycleCode, newStatus);
    }

    // ✅ 예약 추가 (전화번호로 회원 코드 조회 후 저장)
    public Integer createReservation(String bicycleCode, String startTime, String endTime, String phoneNumber,
            String bicycleType, Integer ratePerHour) {
        // 전화번호로 회원 코드 조회
        Integer memberCode = memberMapper.findMemberCodeByPhone(phoneNumber);

        if (memberCode == null) {
            throw new IllegalArgumentException("해당 전화번호로 등록된 회원을 찾을 수 없습니다: " + phoneNumber);
        }

        // 이용 시간 계산 (분 단위를 시간으로 변환)
        double durationHours = calculateDurationHours(startTime, endTime);

        // 총 금액 계산
        int totalAmount = (int) Math.round(ratePerHour * durationHours);

        // 예약 정보 저장
        bicycleMapper.insertReservation(
                bicycleCode,
                startTime,
                endTime,
                memberCode,
                "예약완료",
                bicycleType,
                ratePerHour,
                durationHours,
                totalAmount);

        // ✅ 자전거 상태 변경 (예약됨)
        bicycleMapper.updateBicycleStatus(Integer.parseInt(bicycleCode), "Reserved");

        // ✅ 관리자 서버로 매출 전송
        sendTransactionToAdminServer(phoneNumber, totalAmount, startTime, endTime);

        // TODO: 실제 bookingId 반환 로직 필요 (현재는 임시 값)
        return (int) (System.currentTimeMillis() % 100000);
    }

    // ✅ 관리자 서버로 거래 기록 전송
    private void sendTransactionToAdminServer(String phoneNumber, int amount, String startTime, String endTime) {
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

            String url = "http://localhost:8000/api/transactions/add";
            String merchantCode = "B000000001"; // 바이크웨이 하남점

            // 날짜 포맷 변환 (String "HH:mm" -> LocalDateTime ISO String)
            // startTime, endTime은 "HH:mm" 형식이므로 날짜(오늘)를 붙여야 함
            String today = java.time.LocalDate.now().toString();
            String startDateTime = today + "T" + startTime + ":00";
            String endDateTime = today + "T" + endTime + ":00";

            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("phoneNum", phoneNumber);
            payload.put("merchantCode", merchantCode);
            payload.put("amountUsed", amount);
            payload.put("status", "S");
            payload.put("startDate", startDateTime);
            payload.put("endDate", endDateTime);

            restTemplate.postForObject(url, payload, String.class);
            System.out.println("🚲 자전거 매출 전송 완료: " + amount + "원");

        } catch (Exception e) {
            System.err.println("❌ 자전거 매출 전송 실패: " + e.getMessage());
        }
    }

    // 시간 차이를 시간 단위로 계산 (HH:mm 형식)
    private double calculateDurationHours(String startTime, String endTime) {
        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");

            int startMinutes = Integer.parseInt(startParts[0]) * 60 + Integer.parseInt(startParts[1]);
            int endMinutes = Integer.parseInt(endParts[0]) * 60 + Integer.parseInt(endParts[1]);

            int durationMinutes = endMinutes - startMinutes;
            if (durationMinutes < 0) {
                durationMinutes += 24 * 60; // 자정 넘어가는 경우
            }

            return Math.round(durationMinutes / 60.0 * 100.0) / 100.0; // 소수점 2자리
        } catch (Exception e) {
            return 0.5; // 기본값 30분
        }
    }
}
