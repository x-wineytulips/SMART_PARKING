@Data
@Builder
public class ParkingRequest {
    private String licensePlate;
    private VehicleType vehicleType;
    private String vehicleColor;
    private String vehicleModel;
    private String ownerContact;
    private String preferredLocation;
    private Duration expectedDuration;
    private boolean needsCharging;
    private boolean isValet;
} 