package com.hulahoop.bikewayback.model.dao;

import com.hulahoop.bikewayback.model.dto.BicycleResponseDTO;
import com.hulahoop.bikewayback.model.info.BicycleInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface BicycleMapper {

        // ✅ 필터(자전거/씽씽이/바이크웨이) 기반 가용 자전거 조회
        List<BicycleResponseDTO> findAvailableBicyclesByLocation(
                        @Param("centerLat") double centerLat,
                        @Param("centerLon") double centerLon,
                        @Param("radiusKm") double radiusKm,
                        @Param("typeFilter") String typeFilter);

        // ✅ 자전거 상태 업데이트
        int updateBicycleStatus(@Param("bicycleCode") int bicycleCode,
                        @Param("status") String status);

        // ✅ 예약 추가
        void insertReservation(
                        @Param("bicycleCode") String bicycleCode,
                        @Param("startTime") String startTime,
                        @Param("endTime") String endTime,
                        @Param("memberCode") Integer memberCode,
                        @Param("state") String state,
                        @Param("bicycleType") String bicycleType,
                        @Param("ratePerHour") Integer ratePerHour,
                        @Param("durationHours") Double durationHours,
                        @Param("totalAmount") Integer totalAmount,
                        @Param("transactionNum") Long transactionNum);

        // ✅ 전체 자전거 조회
        List<BicycleInfo> findAllBicycles();

        @Select("SELECT * FROM T_BicycleInfo WHERE bicycle_code = #{code}")
        BicycleInfo findByCode(int code);
}
