public class ParkingLot {
    private String id;
    private String location;
    private List<ParkingSpot> spots;
    private int capacity;

    public ParkingLot(String id, String location, int capacity) {
        this.id = id;
        this.location = location;
        this.capacity = capacity;
        this.spots = new ArrayList<>();
    }

    public ParkingSpot findAvailableSpot(VehicleType type) {
        return spots.stream()
                .filter(spot -> !spot.isOccupied() && spot.getType() == type)
                .findFirst()
                .orElse(null);
    }

    public boolean isFull() {
        return spots.stream().allMatch(ParkingSpot::isOccupied);
    }
} 