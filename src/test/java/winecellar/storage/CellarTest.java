package winecellar.storage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import winecellar.model.Bottle;
import winecellar.model.WineType;
import java.util.List;
import java.util.Optional;

class CellarTest {
    @Test
    void newCellarIsEmpty() {
        Cellar cellar = new Cellar();
        assertTrue(cellar.allBottles().isEmpty());
    }

    @Test
    void bottleAdded() {
        Cellar cellar = new Cellar();
        Bottle bottle = new Bottle("Alberto Maichin", "Bread and Butter", 2008, "Burgundy", WineType.valueOf("WHITE"), Optional.of(50), Optional.of(2030), Optional.of(2033));

        cellar.add(bottle);
        assertEquals(1, cellar.allBottles().size());
    }

    @Test
    void findByProducerReturns() {
        Cellar cellar = new Cellar();
        Bottle bottle1 = new Bottle("Alberto Maichin", "Bread and Butter", 2008, "Burgundy", WineType.valueOf("WHITE"), Optional.of(50), Optional.of(2030), Optional.of(2033));
        Bottle bottle2 = new Bottle("P.A. Larsen", "Mulva", 2015, "Burgundy", WineType.valueOf("WHITE"), Optional.of(50), Optional.of(2030), Optional.of(2033));
        Bottle bottle3 = new Bottle("Dom Perignon", "Extra brut", 2008, "Champagne", WineType.valueOf("WHITE"), Optional.of(50), Optional.of(2030), Optional.of(2033));

        cellar.add(bottle1);
        cellar.add(bottle2);
        cellar.add(bottle3);

        assertEquals(1, cellar.findByProducer("Alberto Maichin").size());
        assertTrue(cellar.findByProducer("Alberto Maichin").contains(bottle1));
    }

    @Test
    void findByProducerReturnsEmpty() {
        Cellar cellar = new Cellar();
        Bottle bottle1 = new Bottle("Alberto Maichin", "Bread and Butter", 2008, "Burgundy", WineType.valueOf("WHITE"), Optional.of(50), Optional.of(2030), Optional.of(2033));
        Bottle bottle2 = new Bottle("P.A. Larsen", "Mulva", 2015, "Burgundy", WineType.valueOf("WHITE"), Optional.of(50), Optional.of(2030), Optional.of(2033));
        Bottle bottle3 = new Bottle("Dom Perignon", "Extra brut", 2008, "Champagne", WineType.valueOf("WHITE"), Optional.of(50), Optional.of(2030), Optional.of(2033));

        cellar.add(bottle1);
        cellar.add(bottle2);
        cellar.add(bottle3);

        assertTrue(cellar.findByProducer("Chateu Margoux").isEmpty());
    }

    @Test
    void allBottlesReturnsCopy() {
        Cellar cellar = new Cellar();
        Bottle bottle1 = new Bottle("Alberto Maichin", "Bread and Butter", 2008, "Burgundy", WineType.valueOf("WHITE"), Optional.of(50), Optional.of(2030), Optional.of(2033));
        Bottle bottle2 = new Bottle("P.A. Larsen", "Mulva", 2015, "Burgundy", WineType.valueOf("WHITE"), Optional.of(50), Optional.of(2030), Optional.of(2033));
        Bottle bottle3 = new Bottle("Dom Perignon", "Extra brut", 2008, "Champagne", WineType.valueOf("WHITE"), Optional.of(50), Optional.of(2030), Optional.of(2033));
        Bottle bottle4 = new Bottle("Heinrich", "Heinrich Pinot", 2025, "Burgundy", WineType.valueOf("WHITE"), Optional.of(50), Optional.of(2030), Optional.of(2033));

        cellar.add(bottle1);
        cellar.add(bottle2);
        cellar.add(bottle3);

        List<Bottle> bottles = cellar.allBottles();
        
        cellar.add(bottle4);

        assertEquals(3, bottles.size());

    }

}
