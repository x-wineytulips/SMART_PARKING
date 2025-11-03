@Entity
@Data
@Builder
public class Bill {
    @Id
    private String billId;
    
    @OneToOne
    private ParkingTicket ticket;
    
    private LocalDateTime exitTime;
    private Duration parkingDuration;
    private double baseFee;
    private double additionalCharges;
    private double discount;
    private double totalAmount;
    private BillStatus status;
    
    @OneToOne(mappedBy = "bill")
    private Payment payment;
} 