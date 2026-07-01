package winecellar.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BottleTest {
    @Test
    void validBottleStoresFields() {
        Bottle bottle = new Bottle("Alberto Maichin", "Bread and Butter", 2008, "Burgundy");
        assertEquals("Alberto Maichin", bottle.producer());
        assertEquals("Bread and Butter", bottle.name());
        assertEquals(2008, bottle.vintage());
        assertEquals("Burgundy", bottle.region());
    }

    @Test
    void nullProducerIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle(null, "Bread and Butter", 2008, "Burgundy"));
    }

    @Test
    void blankNameIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "", 2008, "Burgundy"));
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", " ", 2008, "Burgundy"));
    }

    @Test
    void negativeVintageIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", -1, "Burgundy"));
    }

}
