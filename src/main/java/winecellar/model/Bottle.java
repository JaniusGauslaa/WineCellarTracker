package winecellar.model;
import java.util.Optional;

public record Bottle(String producer, String name, int vintage, String region, WineType type, Optional<Integer> rating, Optional<Integer> readyYear, Optional<Integer> peakYear) {

    public Bottle {
        int currentYear = java.time.Year.now().getValue();

        if (producer == null || producer.isBlank()) {
            throw new IllegalArgumentException("Producer cannot be null or blank");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }

        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Region cannot be null or blank");
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
                throw new IllegalArgumentException("readyYear cannot be later than peakYear");
            }
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
