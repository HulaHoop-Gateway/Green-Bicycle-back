package com.hulahoop.bikewayback.service;

import com.hulahoop.bikewayback.model.dao.BicycleMapper;
import com.hulahoop.bikewayback.model.dao.ReservationMapper;
import com.hulahoop.bikewayback.model.dto.ReservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReservationExpiryScheduler.class);

    private final ReservationMapper reservationMapper;
    private final BicycleMapper bicycleMapper;

    public ReservationExpiryScheduler(ReservationMapper reservationMapper, BicycleMapper bicycleMapper) {
        this.reservationMapper = reservationMapper;
        this.bicycleMapper = bicycleMapper;
    }

    /**
     * 매 5분마다 만료된 예약을 찾아 자동으로 상태를 변경합니다.
     * - 예약 상태: 예약됨 → 완료됨
     * - 자전거 상태: Reserved → Available
     */
    @Scheduled(fixedRate = 300000) // 5분 (300,000 ms)
    public void processExpiredReservations() {
        try {
            log.info("🔍 만료된 예약 검색 시작...");

            List<ReservationDTO> expiredReservations = reservationMapper.findExpiredReservations();

            if (expiredReservations.isEmpty()) {
                log.info("✅ 만료된 예약 없음");
                return;
            }

            log.info("⏰ 만료된 예약 {}건 발견", expiredReservations.size());

            int successCount = 0;
            for (ReservationDTO reservation : expiredReservations) {
                try {
                    // 예약 상태 업데이트: 예약됨 → 완료됨
                    int updated = reservationMapper.updateReservationState(reservation.getRecordNum(), "완료됨");

                    if (updated > 0) {
                        // 자전거 상태 복구: Reserved → Available
                        bicycleMapper.updateBicycleStatus(reservation.getBicycleCode(), "Available");
                        successCount++;

                        log.info("✔️ 예약 #{} 만료 처리 완료 (자전거: {})",
                                reservation.getRecordNum(), reservation.getBicycleCode());
                    }
                } catch (Exception e) {
                    log.error("❌ 예약 #{} 처리 실패: {}", reservation.getRecordNum(), e.getMessage());
                }
            }

            log.info("🎉 만료 처리 완료: {}/{} 건 성공", successCount, expiredReservations.size());

        } catch (Exception e) {
            log.error("❌ 만료 예약 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
