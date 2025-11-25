package com.hulahoop.bikewayback.controller;

import com.hulahoop.bikewayback.model.dto.BicycleResponseDTO;
import com.hulahoop.bikewayback.model.service.BikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BikeController
 * - Gateway 라우팅(Path=/api/gateway/**, Header=intent=.*bike.* → /api/bikes/dispatch)
 * - 모든 자전거 관련 인텐트를 단일 엔드포인트에서 처리
 * - bike_list / bike_rate / bike_booking_step3 모두 처리
 */
@RestController
@RequestMapping("/api/bikes")
public class BikeController {

    private final BikeService bikeService;

    public BikeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    // ============================================================
    // 1. 단일 엔드포인트: intent 기반 분기 처리
    // ============================================================
    @PostMapping("/dispatch")
    public ResponseEntity<Map<String, Object>> handleBikesFromGateway(
            @RequestBody Map<String, Object> request) {

        String intent = (String) request.get("intent");
        if (intent == null) intent = "bike_list"; // 기본 intent

        // 💡 핵심 개발 원칙: data 맵 추출
        // IntentService에서 전달되는 실제 데이터는 항상 최상위 payload 맵의 data 키 안에 들어있습니다.
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) request.getOrDefault("data", new HashMap<>());

        System.out.println("[BikeController] Dispatch 요청 수신, intent=" + intent + ", data=" + data);

        switch (intent) {

            case "bike_rate":
                return handleBikeRate(data);

            case "bike_booking_step3":
                return handleBikeBooking(data);

            case "bike_list":
            default:
                return handleBikeList(data);
        }
    }

    // ============================================================
    // 2. bike_list 인텐트 처리: 위치 기반 자전거 조회
    // ============================================================
    private ResponseEntity<Map<String, Object>> handleBikeList(Map<String, Object> data) {

        // 💡 data 맵에서 추출하도록 수정
        double lat = data.get("centerLat") == null ? 37.5630 : ((Number) data.get("centerLat")).doubleValue();
        double lon = data.get("centerLon") == null ? 127.1929 : ((Number) data.get("centerLon")).doubleValue();
        double radius = data.get("radiusKm") == null ? 5.0 : ((Number) data.get("radiusKm")).doubleValue();
        String filter = (String) data.get("typeFilter");

        List<BicycleResponseDTO> availableBikes =
                bikeService.findAvailableBikesByLocation(lat, lon, radius, filter);

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
    // 4. bike_booking_step3 인텐트 처리: 최종 예약 확정 및 DB 저장 (Mock)
    // ============================================================
    private ResponseEntity<Map<String, Object>> handleBikeBooking(Map<String, Object> data) {

        // 💡 data 맵에서 추출하도록 수정. 최종 예약을 위한 모든 정보가 담겨있을 것입니다.
        // DB 저장/Mock 로직을 수행합니다. (가이드라인에 따라 Mock 처리)

        // Mock DB 저장 로직: 성공 가정
        // Integer bicycleId = (Integer) data.get("bicycleId"); // 실제 예약 시 ID 대신 Code 등을 사용합니다.

        // TODO: 여기서 bookingContext의 모든 정보를 사용하여 최종 예약 저장 로직을 수행해야 합니다.

        Map<String, Object> response = new HashMap<>();
        response.put("intent", "bike_booking_step3");
        response.put("message", "success"); // 필수 반환 필드: message: "success"
        response.put("bookingId", 12345); // 예약 ID Mock

        return ResponseEntity.ok(response);
    }
}