package winecellar.storage;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import winecellar.model.Location;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PostgresLocationRepository implements LocationRepository {

    private final String url;

    public PostgresLocationRepository(@Value("${spring.datasource.url}") String url) {
        this.url = url;
    }

    public void addLocation(Location location) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO locations (name, description) VALUES (?, ?)")) {
            stmt.setString(1, location.name());

            if (location.description().isPresent()) {
                stmt.setString(2, location.description().get());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public List<Location> allLocations() {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM locations")) {
            ResultSet rs = stmt.executeQuery();
            List<Location> locations = new ArrayList<>();
            Optional<String> description;

            while (rs.next()) {
                String name = rs.getString("name");

                String descriptionValue = rs.getString("description");
                if (rs.wasNull()) {
                    description = Optional.empty();
                } else {
                    description = Optional.of(descriptionValue);
                }

                locations.add(new Location(name, description));
            }

            return locations;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public void removeLocation(int index) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM locations")) {
            ResultSet rs = stmt.executeQuery();

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
                throw new IllegalArgumentException("Your index does not correspond to a location");
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM locations WHERE id = ?")) {
                deleteStmt.setInt(1, id);

                deleteStmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public int getLocationId(int index) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM locations")) {
            ResultSet rs = stmt.executeQuery();

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
                throw new IllegalArgumentException("Your index does not correspond to a location");
            }

            return id;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
    
}
