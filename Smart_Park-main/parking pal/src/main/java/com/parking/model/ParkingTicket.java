@Entity
@Data
@Builder
public class ParkingTicket {
    @Id
    private String ticketId;
    
    @ManyToOne
    private Vehicle vehicle;
    
    @ManyToOne
    private ParkingSpot spot;
    
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Duration expectedDuration;
    private ParkingStatus status;
    
    @OneToMany(mappedBy = "ticket")
    private List<AdditionalService> additionalServices;
    
    @Version
    private Long version;
} 