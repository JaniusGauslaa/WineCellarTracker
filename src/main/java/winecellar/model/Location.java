package winecellar.model;
import java.util.Optional;

public record Location(String name, Optional<String> description) {
    public Location {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or blank.");
        }

        if (description == null) {
            throw new IllegalArgumentException("description cannot be null.");
        } else if (description.isPresent() && description.get().isBlank()) {
            throw new IllegalArgumentException("description cannot be blank.");
        }
    }

    @Override
    public String toString() {
        if (description.isPresent()) {
            return name + " - " + description.get();
        } else {
            return name;
        }
    }
}
