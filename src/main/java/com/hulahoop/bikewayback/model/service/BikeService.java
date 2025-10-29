package com.hulahoop.bikewayback.model.service;

import com.hulahoop.bikewayback.model.dto.BicycleResponseDTO;
import com.hulahoop.bikewayback.model.dao.BicycleMapper;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 🚲 BikeService
 * - 위치 기반 자전거 조회 및 상태 관리 비즈니스 로직 담당
 */
@Service
public class BikeService {

    private final BicycleMapper bicycleMapper;

    public BikeService(BicycleMapper bicycleMapper) {
        this.bicycleMapper = bicycleMapper;
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

    // ✅ 예약 추가
    public void createReservation() {
        bicycleMapper.insertReservation();
    }
}
