package com.parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SMSService smsService;
    
    public void sendParkingConfirmation(ParkingTicket ticket) {
        String message = generateParkingConfirmation(ticket);
        
        // Send email confirmation
        emailService.sendEmail(
            ticket.getVehicle().getOwnerContact(),
            "Parking Confirmation",
            message
        );
        
        // Send SMS confirmation
        smsService.sendSMS(
            ticket.getVehicle().getOwnerContact(),
            generateSMSConfirmation(ticket)
        );
    }
    
    public void sendCheckoutConfirmation(Bill bill) {
        String message = generateCheckoutConfirmation(bill);
        
        // Send email receipt
        emailService.sendEmail(
            bill.getTicket().getVehicle().getOwnerContact(),
            "Parking Receipt",
            message
        );
        
        // Send SMS notification
        smsService.sendSMS(
            bill.getTicket().getVehicle().getOwnerContact(),
            generateSMSReceipt(bill)
        );
    }
} 