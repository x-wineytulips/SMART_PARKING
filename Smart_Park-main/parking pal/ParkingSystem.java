package parking.core;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingSystem {
    private final Map<String, ParkingLot> parkingLots;
    private final Map<String, Reservation> reservations;
    private final Map<String, Vehicle> activeParking;
    private final PricingStrategy pricingStrategy;
    private final NotificationService notificationService;

    public ParkingSystem() {
        this.parkingLots = new ConcurrentHashMap<>();
        this.reservations = new ConcurrentHashMap<>();
        this.activeParking = new ConcurrentHashMap<>();
        this.pricingStrategy = new DynamicPricingStrategy();
        this.notificationService = new NotificationService();
    }

    public ParkingTicket parkVehicle(Vehicle vehicle, String lotId) {
        ParkingLot lot = parkingLots.get(lotId);
        if (lot == null) {
            throw new ParkingException("Invalid parking lot ID");
        }

        ParkingSpot spot = lot.findOptimalSpot(vehicle.getType());
        if (spot == null) {
            throw new ParkingException("No available spots for " + vehicle.getType());
        }

        spot.occupy(vehicle);
        activeParking.put(vehicle.getLicensePlate(), vehicle);
        
        ParkingTicket ticket = new ParkingTicket(
            UUID.randomUUID().toString(),
            vehicle,
            spot,
            lot,
            LocalDateTime.now()
        );

        notificationService.sendParkingConfirmation(ticket);
        return ticket;
    }

    public Bill checkoutVehicle(String ticketId) {
        ParkingTicket ticket = validateTicket(ticketId);
        Vehicle vehicle = ticket.getVehicle();
        ParkingSpot spot = ticket.getSpot();

        spot.vacate();
        activeParking.remove(vehicle.getLicensePlate());

        double fee = pricingStrategy.calculateFee(ticket);
        Bill bill = new Bill(ticket, fee);
        
        notificationService.sendCheckoutConfirmation(bill);
        return bill;
    }

    public Reservation reserveSpot(Vehicle vehicle, String lotId, LocalDateTime startTime) {
        ParkingLot lot = parkingLots.get(lotId);
        if (lot == null) {
            throw new ParkingException("Invalid parking lot ID");
        }

        String reservationId = UUID.randomUUID().toString();
        Reservation reservation = new Reservation(
            reservationId,
            vehicle,
            lot,
            startTime,
            startTime.plusHours(2)  // Default 2-hour reservation
        );

        reservations.put(reservationId, reservation);
        notificationService.sendReservationConfirmation(reservation);
        return reservation;
    }
} 