package winecellar.storage;
import java.util.List;
import java.util.function.Predicate;
import winecellar.model.Bottle;
import winecellar.model.TastingNote;
import java.util.Comparator;

public interface CellarRepository {
    void add(Bottle bottle);

    List<Bottle> allBottles();

    List<Bottle> findBy(Predicate<Bottle> condition);

    List<Bottle> findByProducer(String producer);

    List<Bottle> findByRegion(String region);

    List<Bottle> findByVintage(int vintage);

    List<Bottle> sorted(Comparator<Bottle> comparator);

    void remove(int index);

    void addTastingNote(int bottleIndex, TastingNote tastingNote);

    List<TastingNote> getTastingNotes(int bottleIndex);
}
