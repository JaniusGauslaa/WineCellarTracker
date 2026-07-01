package winecellar.model;
import java.util.Optional;

public record Bottle(String producer, String name, int vintage, String region, WineType type, Optional<Integer> rating) {

    public Bottle {
        if (producer == null || producer.isBlank()) {
            throw new IllegalArgumentException("Producer cannot be null or blank");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }

        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Region cannot be null or blank");
        }

        if (vintage < 1900 || vintage > 2030) {
            throw new IllegalArgumentException("Vintage must be between 1900 and 2030");
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
    }

    @Override
    public String toString() {
        return name + " (" + producer + ") " + vintage + " (" + region + ")";
    }
}
