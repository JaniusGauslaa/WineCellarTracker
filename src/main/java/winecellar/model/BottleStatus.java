package winecellar.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public sealed interface BottleStatus {
    record InCellar() implements BottleStatus {}

    record Consumed(LocalDate date) implements BottleStatus {
        public Consumed {
            LocalDate currentDate = LocalDate.now();

            if (date == null) {
                throw new IllegalArgumentException("date cannot be null.");
            }

            if (date.isAfter(currentDate)) {
                throw new IllegalArgumentException("date cannot be after " + currentDate);
            }
        }
    }

    record Sold(BigDecimal price, LocalDate date) implements BottleStatus {
        public Sold {
            LocalDate currentDate = LocalDate.now();

            if (price == null) {
                throw new IllegalArgumentException("price cannot be null.");
            }

            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("price cannot be zero or negative.");
            }

            if (date == null) {
                throw new IllegalArgumentException("date cannot be null.");
            }

            if (date.isAfter(currentDate)) {
                throw new IllegalArgumentException("date cannot be after " + currentDate);
            }
        }
    }

    record Removed(Optional<String> reason) implements BottleStatus {
        public Removed {
            if (reason == null) {
                throw new IllegalArgumentException("reason cannot be null.");
            }

            if (reason.isPresent() && reason.get().isBlank()) {
                throw new IllegalArgumentException("reason cannot be blank.");
            }
        }
    }
}
