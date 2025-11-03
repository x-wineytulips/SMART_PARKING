public enum VehicleType {
    CAR(1.0),
    MOTORCYCLE(0.5),
    TRUCK(2.0),
    BUS(2.5);

    private final double rate;

    VehicleType(double rate) {
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }
} 