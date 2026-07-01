package winecellar.storage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.Comparator;
import winecellar.model.Bottle;

public class Cellar implements CellarRepository {
    private final List<Bottle> bottles;

    public Cellar() {
        bottles = new ArrayList<Bottle>();
    }

    public void add(Bottle bottle) {
        bottles.add(bottle);
    }

    public List<Bottle> allBottles() {
        return List.copyOf(bottles);
    }

    public List<Bottle> findBy(Predicate<Bottle> condition) {
        return bottles.stream().filter(condition).toList();
    }

    public List<Bottle> findByProducer(String producer) {
        return bottles.stream().filter(bottle -> producer.equals(bottle.producer())).toList();
    }

    public List<Bottle> findByRegion(String region) {
        return bottles.stream().filter(bottle -> region.equals(bottle.region())).toList();
    }

    public List<Bottle> findByVintage(int vintage) {
        return bottles.stream().filter(bottle -> vintage == bottle.vintage()).toList();
    }

    public List<Bottle> sorted(Comparator<Bottle> comparator) {
        List<Bottle> bottlesCopy = new ArrayList<>(bottles);
        bottlesCopy.sort(comparator);
        return bottlesCopy;
    }

    public void remove(int index) {
        if (index < 0 || index >= bottles.size()) {
            throw new IllegalArgumentException("The number you chose does not correspond to a bottle in your cellar.");
        }
        bottles.remove(index);
    }

    public void save(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename));) {
            for (Bottle bottle : bottles) {
                writer.write(bottle.producer() + "," + bottle.name() + "," + bottle.vintage() + "," + bottle.region());
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("An error occured");
        }
    }

    public void load(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bottleInformation = line.split(",");
                Bottle bottle = new Bottle(bottleInformation[0], bottleInformation[1], Integer.parseInt(bottleInformation[2]), bottleInformation[3]);
                bottles.add(bottle);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Starting with an empty cellar");
        } catch (IOException e) {
            System.out.println("An error occured");
        }
    }
}   
