package com.parking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {
    @Autowired
    private ParkingService parkingService;

    @PostMapping("/park")
    public ResponseEntity<ParkingTicket> parkVehicle(@RequestBody ParkingRequest request) {
        ParkingTicket ticket = parkingService.parkVehicle(request);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/checkout/{ticketId}")
    public ResponseEntity<Bill> checkoutVehicle(@PathVariable String ticketId) {
        Bill bill = parkingService.checkoutVehicle(ticketId);
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/availability")
    public ResponseEntity<Map<String, Integer>> getAvailability() {
        return ResponseEntity.ok(parkingService.getAvailability());
    }
} 