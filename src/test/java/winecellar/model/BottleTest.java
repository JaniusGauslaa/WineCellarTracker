package winecellar.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

class BottleTest {
    @Test
    void validBottleStoresFields() {
        Bottle bottle = new Bottle("Alberto Maichin", "Bread and Butter", 2008, "Burgundy", WineType.valueOf("RED"), Optional.of(20));
        assertEquals("Alberto Maichin", bottle.producer());
        assertEquals("Bread and Butter", bottle.name());
        assertEquals(2008, bottle.vintage());
        assertEquals("Burgundy", bottle.region());
        assertEquals(WineType.valueOf("RED"), bottle.type());
        assertEquals(Optional.of(20), bottle.rating());
    }

    @Test
    void nullProducerIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle(null, "Bread and Butter", 2008, "Burgundy", WineType.valueOf("WHITE"), Optional.of(20)));
    }

    @Test
    void blankNameIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "", 2008, "Burgundy", WineType.valueOf("WHITE"), Optional.of(20)));
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", " ", 2008, "Burgundy", WineType.valueOf("WHITE"), Optional.of(20)));
    }

    @Test
    void negativeVintageIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", -1, "Burgundy", WineType.valueOf("WHITE"), Optional.of(20)));
    }

    @Test
    void ratingOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(120)));
    }

}
