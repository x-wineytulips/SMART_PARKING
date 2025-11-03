package com.parking.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class ParkingService {
    @Autowired
    private ParkingLotRepository parkingLotRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private ParkingTicketRepository ticketRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private PaymentService paymentService;

    public ParkingTicket parkVehicle(ParkingRequest request) {
        // Validate request
        validateParkingRequest(request);

        // Find optimal parking spot
        ParkingSpot spot = findOptimalSpot(request.getVehicleType(), request.getPreferredLocation());
        if (spot == null) {
            throw new ParkingException("No available spots for " + request.getVehicleType());
        }

        // Create vehicle record
        Vehicle vehicle = Vehicle.builder()
                .licensePlate(request.getLicensePlate())
                .type(request.getVehicleType())
                .color(request.getVehicleColor())
                .model(request.getVehicleModel())
                .ownerContact(request.getOwnerContact())
                .build();
        vehicleRepository.save(vehicle);

        // Create parking ticket
        ParkingTicket ticket = ParkingTicket.builder()
                .ticketId(generateTicketId())
                .vehicle(vehicle)
                .spot(spot)
                .entryTime(LocalDateTime.now())
                .expectedDuration(request.getExpectedDuration())
                .status(ParkingStatus.ACTIVE)
                .build();
        
        // Update spot status
        spot.occupy(vehicle);
        parkingLotRepository.save(spot.getParkingLot());

        // Send confirmation
        notificationService.sendParkingConfirmation(ticket);
        
        return ticketRepository.save(ticket);
    }

    public Bill checkoutVehicle(String ticketId) {
        ParkingTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Invalid ticket ID: " + ticketId));

        if (ticket.getStatus() != ParkingStatus.ACTIVE) {
            throw new InvalidTicketException("Ticket is not active");
        }

        LocalDateTime exitTime = LocalDateTime.now();
        Duration parkingDuration = Duration.between(ticket.getEntryTime(), exitTime);

        // Calculate base fee
        double baseFee = calculateBaseFee(ticket.getVehicle().getType(), parkingDuration);
        
        // Apply additional charges
        double additionalCharges = calculateAdditionalCharges(ticket);
        
        // Apply discounts if applicable
        double discount = calculateDiscount(ticket);
        
        // Calculate total fee
        double totalFee = baseFee + additionalCharges - discount;

        // Create bill
        Bill bill = Bill.builder()
                .billId(generateBillId())
                .ticket(ticket)
                .exitTime(exitTime)
                .parkingDuration(parkingDuration)
                .baseFee(baseFee)
                .additionalCharges(additionalCharges)
                .discount(discount)
                .totalAmount(totalFee)
                .status(BillStatus.PENDING)
                .build();

        // Process payment
        PaymentResult payment = paymentService.processPayment(bill);
        if (payment.isSuccessful()) {
            // Update spot status
            ticket.getSpot().vacate();
            parkingLotRepository.save(ticket.getSpot().getParkingLot());
            
            // Update ticket status
            ticket.setStatus(ParkingStatus.COMPLETED);
            ticket.setExitTime(exitTime);
            ticketRepository.save(ticket);
            
            bill.setStatus(BillStatus.PAID);
            
            // Send checkout confirmation
            notificationService.sendCheckoutConfirmation(bill);
        }

        return bill;
    }

    private double calculateBaseFee(VehicleType type, Duration duration) {
        double hourlyRate = type.getRate();
        double hours = Math.ceil(duration.toMinutes() / 60.0);
        return hourlyRate * hours;
    }

    private double calculateAdditionalCharges(ParkingTicket ticket) {
        double charges = 0.0;
        
        // Late fee
        if (ticket.getExpectedDuration() != null) {
            Duration overtime = Duration.between(
                ticket.getEntryTime().plus(ticket.getExpectedDuration()),
                LocalDateTime.now()
            );
            if (!overtime.isNegative()) {
                charges += calculateLateFee(overtime);
            }
        }
        
        // Premium location charge
        if (ticket.getSpot().isPremiumLocation()) {
            charges += ticket.getSpot().getPremiumCharge();
        }
        
        // EV Charging fee
        if (ticket.getVehicle().getType() == VehicleType.ELECTRIC && 
            ticket.getSpot().hasChargingPoint()) {
            charges += calculateChargingFee(ticket);
        }
        
        return charges;
    }

    private double calculateDiscount(ParkingTicket ticket) {
        double discount = 0.0;
        
        // Early bird discount
        if (isEarlyBird(ticket.getEntryTime())) {
            discount += 0.1; // 10% discount
        }
        
        // Loyalty program discount
        LoyaltyTier loyaltyTier = getLoyaltyTier(ticket.getVehicle().getOwnerContact());
        discount += loyaltyTier.getDiscountRate();
        
        // Special event discount
        if (isSpecialEvent()) {
            discount += 0.05; // 5% discount
        }
        
        return discount;
    }

    private ParkingSpot findOptimalSpot(VehicleType type, String preferredLocation) {
        List<ParkingLot> lots = parkingLotRepository.findAvailableLots(type);
        
        return lots.stream()
            .filter(lot -> preferredLocation == null || lot.getLocation().equals(preferredLocation))
            .flatMap(lot -> lot.getAvailableSpots(type).stream())
            .min(Comparator.comparing(ParkingSpot::getDistanceToExit))
            .orElse(null);
    }

    private void validateParkingRequest(ParkingRequest request) {
        if (request.getLicensePlate() == null || request.getLicensePlate().trim().isEmpty()) {
            throw new InvalidRequestException("License plate is required");
        }
        
        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new DuplicateVehicleException("Vehicle is already parked");
        }
        
        if (request.getVehicleType() == null) {
            throw new InvalidRequestException("Vehicle type is required");
        }
        
        if (request.getOwnerContact() == null || !isValidPhoneNumber(request.getOwnerContact())) {
            throw new InvalidRequestException("Valid contact number is required");
        }
    }

    private String generateTicketId() {
        return "TKT" + System.currentTimeMillis() + 
               RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }

    private String generateBillId() {
        return "BILL" + System.currentTimeMillis() + 
               RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }
} 