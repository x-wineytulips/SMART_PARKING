public class Vehicle {
    private String licensePlate;
    private VehicleType type;
    private String ticketId;

    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.ticketId = UUID.randomUUID().toString();
    }

    // Getters and setters
} 