package winecellar.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

class BottleTest {
    @Test
    void validBottleStoresFields() {
        Bottle bottle = new Bottle("Alberto Maichin", "Bread and Butter", 2008, "Burgundy", WineType.valueOf("RED"), Optional.of(20), Optional.of(2030), Optional.of(2033), Optional.of(new BigDecimal(149.99)), Optional.of(LocalDate.of(2020, 06, 13)), Optional.of("Vinmonopolet"), new BottleStatus.InCellar(), Optional.empty(), Optional.empty());
        assertEquals("Alberto Maichin", bottle.producer());
        assertEquals("Bread and Butter", bottle.name());
        assertEquals(2008, bottle.vintage());
        assertEquals("Burgundy", bottle.region());
        assertEquals(WineType.valueOf("RED"), bottle.type());
        assertEquals(Optional.of(20), bottle.rating());
        assertEquals(Optional.of(2030), bottle.readyYear());
        assertEquals(Optional.of(2033), bottle.peakYear());
        assertEquals(Optional.of(new BigDecimal(149.99)), bottle.price());
        assertEquals(Optional.of(LocalDate.of(2020, 06, 13)), bottle.purchaseDate());
        assertEquals(Optional.of("Vinmonopolet"), bottle.store());
        assertEquals(new BottleStatus.InCellar(), bottle.status());
    }

    @Test
    void nullProducerIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle(null, "Bread and Butter", 2008, "Burgundy", WineType.valueOf("WHITE"), Optional.of(20), Optional.of(2030), Optional.of(2033), Optional.of(new BigDecimal(149.99)), Optional.of(LocalDate.of(2020, 06, 13)), Optional.of("Vinmonopolet"), new BottleStatus.InCellar(), Optional.empty(), Optional.empty()));
    }

    @Test
    void blankNameIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "", 2008, "Burgundy", WineType.valueOf("WHITE"), Optional.of(20), Optional.of(2030), Optional.of(2033), Optional.of(new BigDecimal(149.99)), Optional.of(LocalDate.of(2020, 06, 13)), Optional.of("Vinmonopolet"), new BottleStatus.InCellar(), Optional.empty(), Optional.empty()));
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", " ", 2008, "Burgundy", WineType.valueOf("WHITE"), Optional.of(20), Optional.of(2030), Optional.of(2033), Optional.of(new BigDecimal(149.99)), Optional.of(LocalDate.of(2020, 06, 13)), Optional.of("Vinmonopolet"), new BottleStatus.InCellar(), Optional.empty(), Optional.empty()));
    }

    @Test
    void negativeVintageIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", -1, "Burgundy", WineType.valueOf("WHITE"), Optional.of(20), Optional.of(2030), Optional.of(2033), Optional.of(new BigDecimal(149.99)), Optional.of(LocalDate.of(2020, 06, 13)), Optional.of("Vinmonopolet"), new BottleStatus.InCellar(), Optional.empty(), Optional.empty()));
    }

    @Test
    void ratingOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(120), Optional.of(2030), Optional.of(2033), Optional.of(new BigDecimal(149.99)), Optional.of(LocalDate.of(2020, 06, 13)), Optional.of("Vinmonopolet"), new BottleStatus.InCellar(), Optional.empty(), Optional.empty()));
    }

    @Test
    void readyYearOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(15), Optional.of(2010), Optional.of(2033), Optional.of(new BigDecimal(149.99)), Optional.of(LocalDate.of(2020, 06, 13)), Optional.of("Vinmonopolet"), new BottleStatus.InCellar(), Optional.empty(), Optional.empty()));
    }

    @Test
    void peakYearOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(15), Optional.of(2030), Optional.of(2200), Optional.of(new BigDecimal(149.99)), Optional.of(LocalDate.of(2020, 06, 13)), Optional.of("Vinmonopolet"), new BottleStatus.InCellar(), Optional.empty(), Optional.empty()));
    }

    @Test
    void peakYearEarlierThanReadyYear() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(15), Optional.of(2030), Optional.of(2029), Optional.of(new BigDecimal(149.99)), Optional.of(LocalDate.of(2020, 06, 13)), Optional.of("Vinmonopolet"), new BottleStatus.InCellar(), Optional.empty(), Optional.empty()));
    }

    @Test
    void negativeAndZeroPriceIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(15), Optional.of(2027), Optional.of(2029), Optional.of(new BigDecimal(0)), Optional.of(LocalDate.of(2020, 06, 13)), Optional.of("Vinmonopolet"), new BottleStatus.InCellar(), Optional.empty(), Optional.empty()));

        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(15), Optional.of(2027), Optional.of(2029), Optional.of(new BigDecimal(-10.49)), Optional.of(LocalDate.of(2020, 06, 13)), Optional.of("Vinmonopolet"), new BottleStatus.InCellar(), Optional.empty(), Optional.empty()));
    }

    @Test
    void invalidPurchaseDate() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(15), Optional.of(2027), Optional.of(2029), Optional.of(new BigDecimal(149.99)), Optional.of(LocalDate.of(1915, 06, 13)), Optional.of("Vinmonopolet"), new BottleStatus.InCellar(), Optional.empty(), Optional.empty()));

        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(15), Optional.of(2027), Optional.of(2029), Optional.of(new BigDecimal(149.99)), Optional.of(LocalDate.of(2027, 06, 13)), Optional.of("Vinmonopolet"), new BottleStatus.InCellar(), Optional.empty(), Optional.empty()));
    }

    @Test
    void blankStore() {
        assertThrows(IllegalArgumentException.class, () -> new Bottle("Alberto Maichin", "Bread and Butter", 2005, "Burgundy", WineType.valueOf("WHITE"), Optional.of(15), Optional.of(2027), Optional.of(2029), Optional.of(new BigDecimal(149.99)), Optional.of(LocalDate.of(2020, 06, 13)), Optional.of(" "), new BottleStatus.InCellar(), Optional.empty(), Optional.empty()));
    }

}
