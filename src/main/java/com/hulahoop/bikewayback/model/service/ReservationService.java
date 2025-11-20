package com.hulahoop.bikewayback.model.service;

import com.hulahoop.bikewayback.model.dao.ReservationMapper;
import com.hulahoop.bikewayback.model.dto.ReservationDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {
    private final ReservationMapper reservationMapper;
    public ReservationService(ReservationMapper reservationMapper) {
        this.reservationMapper = reservationMapper;
    }

    public List<ReservationDTO> getReservations(int memberCode) {
        return reservationMapper.findByMemberCode(memberCode);
    }
}
