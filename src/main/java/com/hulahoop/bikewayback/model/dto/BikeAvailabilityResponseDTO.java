package com.hulahoop.bikewayback.model.dto;

import java.util.List;

/**
 * 🚴 BikeAvailabilityResponseDTO
 * - 대여 가능한 자전거 목록 응답용
 * - Gateway와 LLM 서버가 공통으로 이해하는 구조
 */
public class BikeAvailabilityResponseDTO {

    private int total;                          // 전체 자전거 개수
    private List<BicycleResponseDTO> items;     // 자전거 목록

    // 기본 생성자
    public BikeAvailabilityResponseDTO() {}

    // 전체 필드 생성자
    public BikeAvailabilityResponseDTO(int total, List<BicycleResponseDTO> items) {
        this.total = total;
        this.items = items;
    }

    // Getter & Setter
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<BicycleResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<BicycleResponseDTO> items) {
        this.items = items;
    }
}
