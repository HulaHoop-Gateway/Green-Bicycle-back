package com.hulahoop.bikewayback.controller;

import com.hulahoop.bikewayback.model.dto.ReservationDTO;
import com.hulahoop.bikewayback.model.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    private final ReservationService reservationService;
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{memberCode}")
    public ResponseEntity<List<ReservationDTO>> getReservations(@PathVariable int memberCode) {
        return ResponseEntity.ok(reservationService.getReservations(memberCode));
    }
}
