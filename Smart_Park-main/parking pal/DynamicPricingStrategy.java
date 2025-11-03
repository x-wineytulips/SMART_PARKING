package parking.pricing;

import java.time.Duration;
import java.time.LocalDateTime;

public class DynamicPricingStrategy implements PricingStrategy {
    private static final double BASE_RATE = 2.0;
    private static final double PEAK_MULTIPLIER = 1.5;
    private static final double WEEKEND_MULTIPLIER = 1.3;

    @Override
    public double calculateFee(ParkingTicket ticket) {
        LocalDateTime entryTime = ticket.getEntryTime();
        LocalDateTime exitTime = LocalDateTime.now();
        Duration duration = Duration.between(entryTime, exitTime);

        double basePrice = BASE_RATE * duration.toHours();
        double multiplier = 1.0;

        // Apply peak hour pricing
        if (isPeakHour(entryTime)) {
            multiplier *= PEAK_MULTIPLIER;
        }

        // Apply weekend pricing
        if (isWeekend(entryTime)) {
            multiplier *= WEEKEND_MULTIPLIER;
        }

        return basePrice * multiplier * ticket.getVehicle().getType().getPriceMultiplier();
    }
} 