package com.hulahoop.bikewayback.controller;

import com.hulahoop.bikewayback.model.dao.BicycleMapper;
import com.hulahoop.bikewayback.model.dao.ReservationMapper;
import com.hulahoop.bikewayback.model.dto.BicycleResponseDTO;
import com.hulahoop.bikewayback.model.dto.ReservationDTO;
import com.hulahoop.bikewayback.model.service.BikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BikeController
 * - Gateway 라우팅(Path=/api/gateway/**, Header=intent=.*bike.* →
 * /api/bikes/dispatch)
 * - 모든 자전거 관련 인텐트를 단일 엔드포인트에서 처리
 * - bike_list / bike_rate / bike_booking_step3 / bike_cancel 모두 처리
 */
@RestController
@RequestMapping("/api/bikes")
public class BikeController {

    private final BikeService bikeService;
    private final ReservationMapper reservationMapper;
    private final BicycleMapper bicycleMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public BikeController(BikeService bikeService, ReservationMapper reservationMapper, BicycleMapper bicycleMapper) {
        this.bikeService = bikeService;
        this.reservationMapper = reservationMapper;
        this.bicycleMapper = bicycleMapper;
    }

    // ============================================================
    // 1. 단일 엔드포인트: intent 기반 분기 처리
    // ============================================================
    @PostMapping("/dispatch")
    public ResponseEntity<Map<String, Object>> handleBikesFromGateway(
            @RequestBody Map<String, Object> request) {

        String intent = (String) request.get("intent");
        if (intent == null)
            intent = "bike_list"; // 기본 intent

        // 💡 핵심 개발 원칙: data 맵 추출
        // IntentService에서 전달되는 실제 데이터는 항상 최상위 payload 맵의 data 키 안에 들어있습니다.
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) request.getOrDefault("data", new HashMap<>());

        System.out.println("[BikeController] Dispatch 요청 수신, intent=" + intent + ", data=" + data);

        switch (intent) {

            case "member_check":
                return handleMemberCheck(data);

            case "bike_rate":
                return handleBikeRate(data);

            case "bike_booking_step3":
                return handleBikeBooking(data);

            case "bike_cancel":
                return handleBikeCancel(data);

            case "bike_list":
            default:
                return handleBikeList(data);
        }
    }

    // ============================================================
    // 2. member_check 인텐트 처리: 회원 존재 여부 확인
    // ============================================================
    private ResponseEntity<Map<String, Object>> handleMemberCheck(Map<String, Object> data) {
        String phoneNumber = (String) data.get("phone");

        if (phoneNumber == null || phoneNumber.isBlank()) {
            Map<String, Object> response = new HashMap<>();
            response.put("exists", false);
            return ResponseEntity.ok(response);
        }

        Integer memberCode = bikeService.findMemberCodeByPhone(phoneNumber);

        Map<String, Object> response = new HashMap<>();
        response.put("exists", memberCode != null);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 3. bike_list 인텐트 처리: 위치 기반 자전거 조회
    // ============================================================
    private ResponseEntity<Map<String, Object>> handleBikeList(Map<String, Object> data) {

        // 💡 data 맵에서 추출하도록 수정
        double lat = data.get("centerLat") == null ? 37.5630 : ((Number) data.get("centerLat")).doubleValue();
        double lon = data.get("centerLon") == null ? 127.1929 : ((Number) data.get("centerLon")).doubleValue();
        double radius = data.get("radiusKm") == null ? 5.0 : ((Number) data.get("radiusKm")).doubleValue();
        String filter = (String) data.get("typeFilter");

        List<BicycleResponseDTO> availableBikes = bikeService.findAvailableBikesByLocation(lat, lon, radius, filter);

        Map<String, Object> response = new HashMap<>();
        response.put("intent", "bike_list");
        response.put("total", availableBikes.size());
        response.put("bicycles", availableBikes); // 중요 반환 필드: total, bicycles

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 3. bike_rate 인텐트 처리: 자전거 타입별 요금 조회
    // ============================================================
    private ResponseEntity<Map<String, Object>> handleBikeRate(Map<String, Object> data) {

        // 💡 data 맵에서 추출하도록 수정
        String bicycleType = (String) data.get("bicycleType");
        int ratePerHour = 0;

        if ("자전거".equals(bicycleType)) {
            ratePerHour = 6000;
        } else if ("씽씽이".equals(bicycleType)) {
            ratePerHour = 9000;
        } else {
            ratePerHour = 4500;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("intent", "bike_rate");
        response.put("ratePerHour", ratePerHour); // 중요 반환 필드: ratePerHour

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 4. bike_booking_step3 인텐트 처리: 최종 예약 확정 및 DB 저장
    // ============================================================
    private ResponseEntity<Map<String, Object>> handleBikeBooking(Map<String, Object> data) {

        System.out.println("[BikeController] 예약 요청 데이터: " + data);

        try {
            // 필수 데이터 추출
            String bicycleCode = String.valueOf(data.get("bicycleCode"));
            String startTime = String.valueOf(data.get("startTime"));
            String endTime = String.valueOf(data.get("endTime"));
            String phoneNumber = String.valueOf(data.get("phoneNumber"));
            String bicycleType = String.valueOf(data.get("bicycleType"));
            Object rateObj = data.get("ratePerHour");
            Integer ratePerHour = (rateObj instanceof Number) ? ((Number) rateObj).intValue() : 0;

            // 데이터 유효성 검사
            if (bicycleCode == null || "null".equals(bicycleCode) ||
                    startTime == null || "null".equals(startTime) ||
                    endTime == null || "null".equals(endTime) ||
                    phoneNumber == null || "null".equals(phoneNumber)) {

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("intent", "bike_booking_step3");
                errorResponse.put("message", "error");
                errorResponse.put("error", "필수 데이터가 누락되었습니다: bicycleCode, startTime, endTime, phoneNumber");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 실제 DB 저장
            Integer bookingId = bikeService.createReservation(bicycleCode, startTime, endTime, phoneNumber, bicycleType,
                    ratePerHour);

            Map<String, Object> response = new HashMap<>();
            response.put("intent", "bike_booking_step3");
            response.put("message", "success"); // 필수 반환 필드
            response.put("bookingId", bookingId);

            System.out.println("[BikeController] 예약 성공: bookingId=" + bookingId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 회원을 찾을 수 없는 경우
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("intent", "bike_booking_step3");
            errorResponse.put("message", "error");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            // 기타 오류
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("intent", "bike_booking_step3");
            errorResponse.put("message", "error");
            errorResponse.put("error", "예약 처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // ============================================================
    // 5. bike_cancel 인텐트 처리: 예약 취소
    // ============================================================
    private ResponseEntity<Map<String, Object>> handleBikeCancel(Map<String, Object> data) {
        System.out.println("[BikeController] handleBikeCancel 진입. data: " + data);
        Map<String, Object> result = new HashMap<>();
        result.put("intent", "bike_cancel");

        try {
            // 안전한 캐스팅: JSON 숫자는 Integer 또는 Long일 수 있음
            Object tNumObj = data.get("transactionNum");
            Long transactionNum = (tNumObj instanceof Number) ? ((Number) tNumObj).longValue() : null;

            String memberCode = (String) data.get("memberCode");
            Object amountUsed = data.get("amountUsed");
            Object startDate = data.get("startDate");
            Object endDate = data.get("endDate");

            System.out.println(
                    "[BikeController] 파싱된 데이터 - transactionNum: " + transactionNum + ", memberCode: " + memberCode);

            if (transactionNum == null || memberCode == null) {
                System.out.println("[BikeController] 필수 정보 누락");
                result.put("success", false);
                result.put("message", "필수 정보가 누락되었습니다.");
                return ResponseEntity.badRequest().body(result);
            }

            // 1️⃣ 예약 정보 조회 (transaction_num으로 검색)
            ReservationDTO reservation = reservationMapper.findByTransactionNum(transactionNum);
            System.out.println("[BikeController] 예약 조회 결과: " + reservation);

            if (reservation == null) {
                System.out.println("[BikeController] 예약 없음");
                result.put("success", false);
                result.put("message", "해당 예약을 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(result);
            }

            // 2️⃣ 이미 취소된 예약인지 확인
            if ("취소됨".equals(reservation.getState())) {
                System.out.println("[BikeController] 이미 취소된 예약");
                result.put("success", false);
                result.put("message", "이미 취소된 예약입니다.");
                return ResponseEntity.badRequest().body(result);
            }

            // 3️⃣ 예약 상태 업데이트 (예약됨 → 취소됨)
            int updated = reservationMapper.updateReservationState(reservation.getRecordNum(), "취소됨");
            System.out.println("[BikeController] 예약 상태 업데이트 결과: " + updated);

            if (updated == 0) {
                result.put("success", false);
                result.put("message", "예약 취소 상태 업데이트 실패");
                return ResponseEntity.status(500).body(result);
            }

            // 4️⃣ 자전거 상태 복구 (Reserved → Available)
            int bicycleCode = reservation.getBicycleCode();
            int bikeUpdated = bicycleMapper.updateBicycleStatus(bicycleCode, "Available");
            System.out.println("[BikeController] 자전거 상태 복구 결과 (code=" + bicycleCode + "): " + bikeUpdated);

            // 5️⃣ Admin 서버로 취소 트랜잭션 기록
            String adminUrl = "http://localhost:8000/api/transactions/add";
            System.out.println("[BikeController] Admin 서버 전송 시작: " + adminUrl);

            // 날짜 배열을 String으로 변환 (Blue-back이 [2025, 11, 28] 형태로 보냄)
            String startDateStr = convertDateToString(startDate);
            String endDateStr = convertDateToString(endDate);

            Map<String, Object> transactionData = new HashMap<>();
            transactionData.put("phoneNum", reservation.getPhoneNumber()); // 전화번호 추가
            transactionData.put("memberCode", memberCode);
            transactionData.put("merchantCode", "B000000001"); // 정확한 Bicycle merchant code
            transactionData.put("amountUsed", amountUsed);
            transactionData.put("status", "R"); // Refund/Cancel
            transactionData.put("originalTransactionNum", transactionNum);
            transactionData.put("startDate", startDateStr);
            transactionData.put("endDate", endDateStr);

            try {
                restTemplate.postForObject(adminUrl, transactionData, String.class);
                System.out.println("[BikeController] Admin 서버 전송 성공");
            } catch (Exception e) {
                System.out.println("[BikeController] Admin 서버 전송 실패: " + e.getMessage());
                e.printStackTrace();
                // Admin 전송 실패해도 취소는 성공으로 처리할지 여부 결정 필요. 일단 로그만 남김.
            }

            result.put("success", true);
            result.put("message", "자전거 예약이 성공적으로 취소되었습니다.");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.out.println("[BikeController] 취소 처리 중 예외 발생");
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "취소 처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    // Helper: 날짜 배열을 String으로 변환 (Blue-back이 [2025, 11, 28] 형태로 보냄)
    private String convertDateToString(Object dateObj) {
        if (dateObj instanceof java.util.List) {
            @SuppressWarnings("unchecked")
            java.util.List<Integer> dateList = (java.util.List<Integer>) dateObj;
            if (dateList.size() >= 3) {
                return String.format("%04d-%02d-%02d", dateList.get(0), dateList.get(1), dateList.get(2));
            }
        }
        return dateObj != null ? dateObj.toString() : null;
    }
}