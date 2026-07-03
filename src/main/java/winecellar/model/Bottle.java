package winecellar.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Bottle(String producer, String name, int vintage, String region, WineType type, Optional<Integer> rating, Optional<Integer> readyYear, Optional<Integer> peakYear, Optional<BigDecimal> price, Optional<LocalDate> purchaseDate, Optional<String> store, BottleStatus status) {

    public Bottle {
        int currentYear = java.time.Year.now().getValue();

        if (producer == null || producer.isBlank()) {
            throw new IllegalArgumentException("Producer cannot be null or blank.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank.");
        }

        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Region cannot be null or blank.");
        }

        int minYear = currentYear - 100;
        int maxYear = currentYear;
        if (vintage < minYear || vintage > maxYear) {
            throw new IllegalArgumentException("Vintage must be between " + minYear + " and " + maxYear + ".");
        }

        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }

        if (rating == null) {
            throw new IllegalArgumentException("Rating cannot be null.");
        } else {
            if (rating.isPresent() && (rating.get() < 1 || rating.get() > 100)) {
                throw new IllegalArgumentException("Rating must be between 1-100.");
            }
        }

        if (readyYear == null) {
            throw new IllegalArgumentException("readyYear cannot be null.");
        } else {
            minYear = currentYear - 10;
            maxYear = currentYear + 100;
            if (readyYear.isPresent() && (readyYear.get() < minYear || readyYear.get() > maxYear)) {
                throw new IllegalArgumentException("readyYear must be between " + minYear + " and " + maxYear + ".");
            }
        }

        if (peakYear == null) {
            throw new IllegalArgumentException("peakYear cannot be null.");
        } else {
            minYear = currentYear - 10;
            maxYear = currentYear + 100;
            if (peakYear.isPresent() && (peakYear.get() < minYear || peakYear.get() > maxYear)) {
                throw new IllegalArgumentException("peakYear must be between " + minYear + " and " + maxYear + ".");
            }
        }

        if (readyYear.isPresent() && peakYear.isPresent()) {
            if (readyYear.get() > peakYear.get()) {
                throw new IllegalArgumentException("readyYear cannot be later than peakYear.");
            }
        }

        if (price == null) {
            throw new IllegalArgumentException("price cannot be null.");
        } else {
            if (price.isPresent() && price.get().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("price cannot be less than or equal to zero.");
            }
        }

        if (purchaseDate == null) {
            throw new IllegalArgumentException("purchaseDate cannot be null.");
        } else {
            LocalDate minDate = LocalDate.now().minusYears(100);
            LocalDate maxDate = LocalDate.now();

            if (purchaseDate.isPresent() && (purchaseDate.get().isBefore(minDate) || purchaseDate.get().isAfter(maxDate))) {
                throw new IllegalArgumentException("purchaseDate must be between " + minDate + " and " + maxDate + ".");
            }
        }

        if (store == null) {
            throw new IllegalArgumentException("store cannot be null.");
        } else if (store.isPresent() && store.get().isBlank()) {
            throw new IllegalArgumentException("store cannot be blank.");
        }

        if (status == null) {
            throw new IllegalArgumentException("status cannot be null.");
        }
    }

    public String maturityStatus() {
        int currentYear = java.time.Year.now().getValue();
        String maturityString = "";

        if (readyYear.isPresent() && peakYear.isPresent()) {
            if (readyYear.get() > currentYear) {
                maturityString = "Too young";
            } else if (readyYear.get() <= currentYear && peakYear.get() >= currentYear) {
                maturityString = "Ready";
            } else if (peakYear.get() < currentYear) {
                maturityString = "Past peak";
            }
        } else {
            if (readyYear.isEmpty() && peakYear.isEmpty()) {
                maturityString = "No drink window";
            } else {
                if (readyYear.isPresent()) {
                    maturityString = readyYear.get() > currentYear ? "Too young" : "Ready";
                }
                if (peakYear.isPresent()) {
                    maturityString = peakYear.get() >= currentYear ? "Ready" : "Past peak";
                }
            }
        }

        return maturityString;
    }

    @Override
    public String toString() {
        return name + " (" + producer + ") " + vintage + " (" + region + ")";
    }
}
