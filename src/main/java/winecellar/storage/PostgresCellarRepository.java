package winecellar.storage;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import winecellar.model.Bottle;

public class PostgresCellarRepository implements CellarRepository {

    private final String url;

    public PostgresCellarRepository(String url) {
        this.url = url;
    }

    public void add(Bottle bottle) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(
             "INSERT INTO bottles (producer, name, vintage, region) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, bottle.producer());
            stmt.setString(2, bottle.name());
            stmt.setInt(3, bottle.vintage());
            stmt.setString(4, bottle.region());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Bottle> allBottles() {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bottles");
             ResultSet rs = stmt.executeQuery()) {
            List<Bottle> bottles = new ArrayList<>();
            while (rs.next()) {
                Bottle bottle = new Bottle(rs.getString("producer"), rs.getString("name"), rs.getInt("vintage"), rs.getString("region"));
                bottles.add(bottle);
            }
            return bottles;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public void remove(int index) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bottles");
             ResultSet rs = stmt.executeQuery()) {
            int i = 0;
            int id = -1;
            while (rs.next()) {
                if (i == index) {
                    id = rs.getInt("id");
                    break;
                }
                i++;
            }

            if (id == -1) {
                throw new IllegalArgumentException("Your index does not correspond to a bottle in your cellar");
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM bottles WHERE id = ?")) {
                deleteStmt.setInt(1, id);
                deleteStmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException();
            }
            
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public List<Bottle> findByProducer(String producer) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bottles WHERE producer = ?")) {
            stmt.setString(1, producer);
            ResultSet rs = stmt.executeQuery();
            List<Bottle> bottles = new ArrayList<>();

            while (rs.next()) {
                Bottle bottle = new Bottle(rs.getString("producer"), rs.getString("name"), rs.getInt("vintage"), rs.getString("region"));
                bottles.add(bottle);
            }

            return bottles;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public List<Bottle> findByRegion(String region) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bottles WHERE region = ?")) {
            stmt.setString(1, region);
            ResultSet rs = stmt.executeQuery();
            List<Bottle> bottles = new ArrayList<>();

            while (rs.next()) {
                Bottle bottle = new Bottle(rs.getString("producer"), rs.getString("name"), rs.getInt("vintage"), rs.getString("region"));
                bottles.add(bottle);
            }

            return bottles;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public List<Bottle> findByVintage(int vintage) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bottles WHERE vintage = ?")) {
            stmt.setInt(1, vintage);
            ResultSet rs = stmt.executeQuery();
            List<Bottle> bottles = new ArrayList<>();

            while (rs.next()) {
                Bottle bottle = new Bottle(rs.getString("producer"), rs.getString("name"), rs.getInt("vintage"), rs.getString("region"));
                bottles.add(bottle);
            }

            return bottles;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public List<Bottle> findBy(Predicate<Bottle> condition) {
        List<Bottle> bottlesCopy = new ArrayList<>(allBottles());
        return bottlesCopy.stream().filter(condition).toList();
    }

    public List<Bottle> sorted(Comparator<Bottle> comparator) {
        List<Bottle> bottlesCopy = new ArrayList<>(allBottles());
        bottlesCopy.sort(comparator);
        return bottlesCopy;
    }

}
