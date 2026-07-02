package winecellar.model;
import java.time.LocalDate;
import java.util.Optional;

public record TastingNote(String note, LocalDate date, Optional<Integer> rating) {
    public TastingNote {
        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException("Note cannot be null or blank.");
        }

        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        } else {
            LocalDate currentDate = LocalDate.now();
            if (date.isAfter(currentDate)) {
                throw new IllegalArgumentException("Date cannot be later than " + currentDate);
            }
        }

        if (rating == null) {
            throw new IllegalArgumentException("Rating cannot be null.");
        } else {
            if (rating.isPresent() && (rating.get() < 1 || rating.get() > 100)) {
                throw new IllegalArgumentException("Rating must be between 1-100.");
            }
        }
    }
}
