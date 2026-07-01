package winecellar.model;

public record Bottle(String producer, String name, int vintage, String region) {

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
    }

    @Override
    public String toString() {
        return name + " (" + producer + ") " + vintage + " (" + region + ")";
    }
}
