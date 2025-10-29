package com.hulahoop.bikewayback.controller;

import com.hulahoop.bikewayback.model.dto.BicycleResponseDTO;
import com.hulahoop.bikewayback.model.service.BikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🚲 BikeController
 * - Gateway 및 내부 API 요청을 처리
 * - /api/bikes/dispatch 구조로 영화 서비스와 통일
 * - 좌표 누락 시 기본값(하남 미사)을 자동 설정
 * - 응답을 { total, bicycles } 구조로 반환
 */
@RestController
@RequestMapping("/api/bikes")  // ✅ 영화 서비스와 구조 통일
public class BikeController {

    private final BikeService bikeService;

    public BikeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    // ============================================================
    // ✅ 1️⃣ Gateway 엔드포인트 (/api/bikes/dispatch)
    // ============================================================
    /**
     * 게이트웨이에서 호출되는 자전거 검색 API
     * - 좌표 누락 시 기본값(하남 미사 중심)
     * - "자전거", "씽씽이", "바이크웨이" 필터 지원
     */
    @PostMapping("/dispatch")
    public ResponseEntity<Map<String, Object>> dispatchBikesFromGateway(
            @RequestBody LocationSearchRequest request) {
        System.out.println("🚲 [BikeController] Gateway dispatch 요청 수신");

        // 💡 좌표 누락 시 기본값 설정 (하남 미사 중심)
        if (request.getCenterLat() == 0) request.setCenterLat(37.5630);
        if (request.getCenterLon() == 0) request.setCenterLon(127.1929);
        if (request.getRadiusKm() <= 0) request.setRadiusKm(5.0);

        System.out.printf("📍 검색 좌표: (%.6f, %.6f), 반경: %.2f km, 필터: %s%n",
                request.getCenterLat(), request.getCenterLon(), request.getRadiusKm(), request.getTypeFilter());

        // 💡 BikeService 호출
        List<BicycleResponseDTO> availableBikes = bikeService.findAvailableBikesByLocation(
                request.getCenterLat(),
                request.getCenterLon(),
                request.getRadiusKm(),
                request.getTypeFilter()
        );

        System.out.println("✅ 검색된 자전거 개수: " + availableBikes.size());

        // 💡 게이트웨이가 파싱 가능한 구조로 응답 감싸기
        Map<String, Object> response = new HashMap<>();
        response.put("total", availableBikes.size());
        response.put("bicycles", availableBikes);

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // ✅ 2️⃣ 내부 테스트용 엔드포인트 (/internal/bike/searchAvailable)
    // ============================================================
    /**
     * 내부 API: 중심 좌표와 반경으로 자전거 조회 (게이트웨이와 동일한 로직)
     */
    @PostMapping("/internal/bike/searchAvailable")
    public ResponseEntity<List<BicycleResponseDTO>> searchAvailableBikes(
            @RequestBody LocationSearchRequest request) {

        if (request.getCenterLat() == 0) request.setCenterLat(37.5630);
        if (request.getCenterLon() == 0) request.setCenterLon(127.1929);
        if (request.getRadiusKm() <= 0) request.setRadiusKm(5.0);

        System.out.printf("📍 [Internal] 검색 좌표: (%.6f, %.6f), 반경: %.2f km, 필터: %s%n",
                request.getCenterLat(), request.getCenterLon(), request.getRadiusKm(), request.getTypeFilter());

        List<BicycleResponseDTO> availableBikes = bikeService.findAvailableBikesByLocation(
                request.getCenterLat(),
                request.getCenterLon(),
                request.getRadiusKm(),
                request.getTypeFilter()
        );

        System.out.println("✅ [Internal] 검색된 자전거 개수: " + availableBikes.size());
        return ResponseEntity.ok(availableBikes);
    }

    // ============================================================
    // ✅ DTO (Request Body)
    // ============================================================
    /**
     * 위치 기반 자전거 검색 요청 DTO
     */
    public static class LocationSearchRequest {
        private double centerLat;   // 중심 위도
        private double centerLon;   // 중심 경도
        private double radiusKm = 1.0;  // 검색 반경 (기본값 1km)
        private String typeFilter;  // 🔥 "자전거", "씽씽이", "바이크웨이" 등 필터

        // Getter / Setter
        public double getCenterLat() { return centerLat; }
        public void setCenterLat(double centerLat) { this.centerLat = centerLat; }

        public double getCenterLon() { return centerLon; }
        public void setCenterLon(double centerLon) { this.centerLon = centerLon; }

        public double getRadiusKm() { return radiusKm; }
        public void setRadiusKm(double radiusKm) { this.radiusKm = radiusKm; }

        public String getTypeFilter() { return typeFilter; }
        public void setTypeFilter(String typeFilter) { this.typeFilter = typeFilter; }
    }
}
