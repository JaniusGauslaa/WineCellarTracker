package winecellar.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

class BottleTest {
    @Test
    void validBottleStoresFields() {
        Bottle bottle = new Bottle("Alberto Maichin", "Bread and Butter", 2008, "Burgundy", WineType.valueOf("RED"), Optional.of(20), Optional.of(2030), Optional.of(2033));
        assertEquals("Alberto Maichin", bottle.producer());
        assertEquals("Bread and Butter", bottle.name());
        assertEquals(2008, bottle.vintage());
        assertEquals("Burgundy", bottle.region());
        assertEquals(WineType.valueOf("RED"), bottle.type());
        assertEquals(Optional.of(20), bottle.rating());
        assertEquals(Optional.of(2030), bottle.readyYear());
        assertEquals(Optional.of(2033), bottle.peakYear());
    }

    @Test
    void nullProducerIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle(null, "Bread and Butter", 2008, "Burgundy", WineType.valueOf("WHITE"), Optional.of(20), Optional.of(2030), Optional.of(2033)));
    }

    @Test
    void blankNameIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "", 2008, "Burgundy", WineType.valueOf("WHITE"), Optional.of(20), Optional.of(2030), Optional.of(2033)));
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", " ", 2008, "Burgundy", WineType.valueOf("WHITE"), Optional.of(20), Optional.of(2030), Optional.of(2033)));
    }

    @Test
    void negativeVintageIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", -1, "Burgundy", WineType.valueOf("WHITE"), Optional.of(20), Optional.of(2030), Optional.of(2033)));
    }

    @Test
    void ratingOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(120), Optional.of(2030), Optional.of(2033)));
    }

    @Test
    void readyYearOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(15), Optional.of(2010), Optional.of(2033)));
    }

    @Test
    void peakYearOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(15), Optional.of(2030), Optional.of(2200)));
    }

    @Test
    void peakYearEarlierThanReadyYear() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(15), Optional.of(2030), Optional.of(2029)));
    }

}
