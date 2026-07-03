package winecellar.storage;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import winecellar.model.Bottle;
import winecellar.model.BottleStatus;
import winecellar.model.TastingNote;
import winecellar.model.WineType;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDate;

@Component
public class PostgresCellarRepository implements CellarRepository {

    private final String url;

    public PostgresCellarRepository(@Value("${spring.datasource.url}") String url) {
        this.url = url;
    }

    public void add(Bottle bottle) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(
             "INSERT INTO bottles (producer, name, vintage, region, type, rating, ready_year, peak_year, price, purchase_date, store, status, status_date, status_price, status_reason, location_id, bin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, bottle.producer());
            stmt.setString(2, bottle.name());
            stmt.setInt(3, bottle.vintage());
            stmt.setString(4, bottle.region());
            stmt.setString(5, bottle.type().name());

            if (bottle.rating().isPresent()) {
                stmt.setInt(6, bottle.rating().get());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            if (bottle.readyYear().isPresent()) {
                stmt.setInt(7, bottle.readyYear().get());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            if (bottle.peakYear().isPresent()) {
                stmt.setInt(8, bottle.peakYear().get());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            if (bottle.price().isPresent()) {
                stmt.setBigDecimal(9, bottle.price().get());
            } else {
                stmt.setNull(9, Types.DECIMAL);
            }

            if (bottle.purchaseDate().isPresent()) {
                stmt.setDate(10, Date.valueOf(bottle.purchaseDate().get()));
            } else {
                stmt.setNull(10, Types.DATE);
            }

            if (bottle.store().isPresent()) {
                stmt.setString(11, bottle.store().get());
            } else {
                stmt.setNull(11, Types.VARCHAR);
            }

            if (bottle.status() instanceof BottleStatus.InCellar) {
                stmt.setString(12, "IN_CELLAR");
                stmt.setNull(13, Types.DATE);
                stmt.setNull(14, Types.DECIMAL);
                stmt.setNull(15, Types.VARCHAR);
            } else if (bottle.status() instanceof BottleStatus.Consumed c) {
                stmt.setString(12, "CONSUMED");
                stmt.setDate(13, Date.valueOf(c.date()));
                stmt.setNull(14, Types.DECIMAL);
                stmt.setNull(15, Types.VARCHAR);
            } else if (bottle.status() instanceof BottleStatus.Sold s) {
                stmt.setString(12, "SOLD");
                stmt.setDate(13, Date.valueOf(s.date()));
                stmt.setBigDecimal(14, s.price());
                stmt.setNull(15, Types.VARCHAR);
            } else if (bottle.status() instanceof BottleStatus.Removed r) {
                stmt.setString(12, "REMOVED");
                stmt.setNull(13, Types.DATE);
                stmt.setNull(14, Types.DECIMAL);

                if (r.reason().isPresent()) {
                    stmt.setString(15, r.reason().get());
                } else {
                    stmt.setNull(15, Types.VARCHAR);
                }
                
            }

            if (bottle.locationId().isPresent()) {
                stmt.setInt(16, bottle.locationId().get());
            } else {
                stmt.setNull(16, Types.INTEGER);
            }

            if (bottle.bin().isPresent()) {
                stmt.setString(17, bottle.bin().get());
            } else {
                stmt.setNull(17, Types.VARCHAR);
            }

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
                int ratingValue = rs.getInt("rating");
                Optional<Integer> rating = rs.wasNull() ? Optional.empty() : Optional.of(ratingValue);

                int readyYearValue = rs.getInt("ready_year");
                Optional<Integer> readyYear = rs.wasNull() ? Optional.empty() : Optional.of(readyYearValue);

                int peakYearValue = rs.getInt("peak_year");
                Optional<Integer> peakYear = rs.wasNull() ? Optional.empty() : Optional.of(peakYearValue);

                BigDecimal priceValue = rs.getBigDecimal("price");
                Optional<BigDecimal> price = rs.wasNull() ? Optional.empty() : Optional.of(priceValue);

                Date purchaseDateValue = rs.getDate("purchase_date");
                Optional<LocalDate> purchaseDate = purchaseDateValue == null ? Optional.empty() : Optional.of(purchaseDateValue.toLocalDate());

                String storeValue = rs.getString("store");
                Optional<String> store = storeValue == null ? Optional.empty() : Optional.of(storeValue);

                String statusValue = rs.getString("status");
                BottleStatus status;
                LocalDate statusDate;
                BigDecimal statusPrice;
                Optional<String> statusReason;

                switch (statusValue) {
                    case "IN_CELLAR":
                        status = new BottleStatus.InCellar();

                        break;
                    case "CONSUMED":
                        statusDate = rs.getDate("status_date").toLocalDate();
                        status = new BottleStatus.Consumed(statusDate);
                        
                        break;
                    case "SOLD":
                        statusDate = rs.getDate("status_date").toLocalDate();
                        statusPrice = rs.getBigDecimal("status_price");
                        status = new BottleStatus.Sold(statusPrice, statusDate);
                        
                        break;
                    case "REMOVED":
                        String statusReasonValue = rs.getString("status_reason");
                        statusReason = statusReasonValue == null ? Optional.empty() : Optional.of(statusReasonValue);
                        status = new BottleStatus.Removed(statusReason);
                        
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown status: " + statusValue);

                }

                int locationIdValue = rs.getInt("location_id");
                Optional<Integer> locationId = rs.wasNull() ? Optional.empty() : Optional.of(locationIdValue);

                String binValue = rs.getString("bin");
                Optional<String> bin = binValue == null ? Optional.empty() : Optional.of(binValue);

                Bottle bottle = new Bottle(rs.getString("producer"), rs.getString("name"), rs.getInt("vintage"), rs.getString("region"), WineType.valueOf(rs.getString("type")), rating, readyYear, peakYear, price, purchaseDate, store, status, locationId, bin);
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
                int ratingValue = rs.getInt("rating");
                Optional<Integer> rating = rs.wasNull() ? Optional.empty() : Optional.of(ratingValue);

                int readyYearValue = rs.getInt("ready_year");
                Optional<Integer> readyYear = rs.wasNull() ? Optional.empty() : Optional.of(readyYearValue);

                int peakYearValue = rs.getInt("peak_year");
                Optional<Integer> peakYear = rs.wasNull() ? Optional.empty() : Optional.of(peakYearValue);

                BigDecimal priceValue = rs.getBigDecimal("price");
                Optional<BigDecimal> price = rs.wasNull() ? Optional.empty() : Optional.of(priceValue);

                Date purchaseDateValue = rs.getDate("purchase_date");
                Optional<LocalDate> purchaseDate = purchaseDateValue == null ? Optional.empty() : Optional.of(purchaseDateValue.toLocalDate());

                String storeValue = rs.getString("store");
                Optional<String> store = storeValue == null ? Optional.empty() : Optional.of(storeValue);

                String statusValue = rs.getString("status");
                BottleStatus status;
                LocalDate statusDate;
                BigDecimal statusPrice;
                Optional<String> statusReason;

                switch (statusValue) {
                    case "IN_CELLAR":
                        status = new BottleStatus.InCellar();

                        break;
                    case "CONSUMED":
                        statusDate = rs.getDate("status_date").toLocalDate();
                        status = new BottleStatus.Consumed(statusDate);
                        
                        break;
                    case "SOLD":
                        statusDate = rs.getDate("status_date").toLocalDate();
                        statusPrice = rs.getBigDecimal("status_price");
                        status = new BottleStatus.Sold(statusPrice, statusDate);
                        
                        break;
                    case "REMOVED":
                        String statusReasonValue = rs.getString("status_reason");
                        statusReason = statusReasonValue == null ? Optional.empty() : Optional.of(statusReasonValue);
                        status = new BottleStatus.Removed(statusReason);
                        
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown status: " + statusValue);

                }

                int locationIdValue = rs.getInt("location_id");
                Optional<Integer> locationId = rs.wasNull() ? Optional.empty() : Optional.of(locationIdValue);

                String binValue = rs.getString("bin");
                Optional<String> bin = binValue == null ? Optional.empty() : Optional.of(binValue);

                Bottle bottle = new Bottle(rs.getString("producer"), rs.getString("name"), rs.getInt("vintage"), rs.getString("region"), WineType.valueOf(rs.getString("type")), rating, readyYear, peakYear, price, purchaseDate, store, status, locationId, bin);
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
                int ratingValue = rs.getInt("rating");
                Optional<Integer> rating = rs.wasNull() ? Optional.empty() : Optional.of(ratingValue);

                int readyYearValue = rs.getInt("ready_year");
                Optional<Integer> readyYear = rs.wasNull() ? Optional.empty() : Optional.of(readyYearValue);

                int peakYearValue = rs.getInt("peak_year");
                Optional<Integer> peakYear = rs.wasNull() ? Optional.empty() : Optional.of(peakYearValue);

                BigDecimal priceValue = rs.getBigDecimal("price");
                Optional<BigDecimal> price = rs.wasNull() ? Optional.empty() : Optional.of(priceValue);

                Date purchaseDateValue = rs.getDate("purchase_date");
                Optional<LocalDate> purchaseDate = purchaseDateValue == null ? Optional.empty() : Optional.of(purchaseDateValue.toLocalDate());

                String storeValue = rs.getString("store");
                Optional<String> store = storeValue == null ? Optional.empty() : Optional.of(storeValue);

                String statusValue = rs.getString("status");
                BottleStatus status;
                LocalDate statusDate;
                BigDecimal statusPrice;
                Optional<String> statusReason;

                switch (statusValue) {
                    case "IN_CELLAR":
                        status = new BottleStatus.InCellar();

                        break;
                    case "CONSUMED":
                        statusDate = rs.getDate("status_date").toLocalDate();
                        status = new BottleStatus.Consumed(statusDate);
                        
                        break;
                    case "SOLD":
                        statusDate = rs.getDate("status_date").toLocalDate();
                        statusPrice = rs.getBigDecimal("status_price");
                        status = new BottleStatus.Sold(statusPrice, statusDate);
                        
                        break;
                    case "REMOVED":
                        String statusReasonValue = rs.getString("status_reason");
                        statusReason = statusReasonValue == null ? Optional.empty() : Optional.of(statusReasonValue);
                        status = new BottleStatus.Removed(statusReason);
                        
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown status: " + statusValue);

                }

                int locationIdValue = rs.getInt("location_id");
                Optional<Integer> locationId = rs.wasNull() ? Optional.empty() : Optional.of(locationIdValue);

                String binValue = rs.getString("bin");
                Optional<String> bin = binValue == null ? Optional.empty() : Optional.of(binValue);

                Bottle bottle = new Bottle(rs.getString("producer"), rs.getString("name"), rs.getInt("vintage"), rs.getString("region"), WineType.valueOf(rs.getString("type")), rating, readyYear, peakYear, price, purchaseDate, store, status, locationId, bin);
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
                int ratingValue = rs.getInt("rating");
                Optional<Integer> rating = rs.wasNull() ? Optional.empty() : Optional.of(ratingValue);

                int readyYearValue = rs.getInt("ready_year");
                Optional<Integer> readyYear = rs.wasNull() ? Optional.empty() : Optional.of(readyYearValue);

                int peakYearValue = rs.getInt("peak_year");
                Optional<Integer> peakYear = rs.wasNull() ? Optional.empty() : Optional.of(peakYearValue);

                BigDecimal priceValue = rs.getBigDecimal("price");
                Optional<BigDecimal> price = rs.wasNull() ? Optional.empty() : Optional.of(priceValue);

                Date purchaseDateValue = rs.getDate("purchase_date");
                Optional<LocalDate> purchaseDate = purchaseDateValue == null ? Optional.empty() : Optional.of(purchaseDateValue.toLocalDate());

                String storeValue = rs.getString("store");
                Optional<String> store = storeValue == null ? Optional.empty() : Optional.of(storeValue);

                String statusValue = rs.getString("status");
                BottleStatus status;
                LocalDate statusDate;
                BigDecimal statusPrice;
                Optional<String> statusReason;

                switch (statusValue) {
                    case "IN_CELLAR":
                        status = new BottleStatus.InCellar();

                        break;
                    case "CONSUMED":
                        statusDate = rs.getDate("status_date").toLocalDate();
                        status = new BottleStatus.Consumed(statusDate);
                        
                        break;
                    case "SOLD":
                        statusDate = rs.getDate("status_date").toLocalDate();
                        statusPrice = rs.getBigDecimal("status_price");
                        status = new BottleStatus.Sold(statusPrice, statusDate);
                        
                        break;
                    case "REMOVED":
                        String statusReasonValue = rs.getString("status_reason");
                        statusReason = statusReasonValue == null ? Optional.empty() : Optional.of(statusReasonValue);
                        status = new BottleStatus.Removed(statusReason);
                        
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown status: " + statusValue);

                }

                int locationIdValue = rs.getInt("location_id");
                Optional<Integer> locationId = rs.wasNull() ? Optional.empty() : Optional.of(locationIdValue);

                String binValue = rs.getString("bin");
                Optional<String> bin = binValue == null ? Optional.empty() : Optional.of(binValue);

                Bottle bottle = new Bottle(rs.getString("producer"), rs.getString("name"), rs.getInt("vintage"), rs.getString("region"), WineType.valueOf(rs.getString("type")), rating, readyYear, peakYear, price, purchaseDate, store, status, locationId, bin);
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

    public void addTastingNote(int bottleIndex, TastingNote tastingNote) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bottles")) {
            ResultSet rs = stmt.executeQuery();
            int index = 0;
            int id = -1;

            while (rs.next()) {
                if (bottleIndex == index) {
                    id = rs.getInt("id");
                    break;
                }
                index++;
            }

            if (id == -1) {
                throw new IllegalArgumentException("Your index does not correspond to a bottle in your cellar.");
            }

            try (PreparedStatement addStmt = conn.prepareStatement("INSERT INTO tasting_notes (bottle_id, note, date, rating) VALUES (?, ?, ?, ?)")) {
                addStmt.setInt(1, id);
                addStmt.setString(2, tastingNote.note());
                addStmt.setDate(3, Date.valueOf(tastingNote.date()));

                if (tastingNote.rating().isPresent()) {
                    addStmt.setInt(4, tastingNote.rating().get());
                } else {
                    addStmt.setNull(4, Types.INTEGER);
                }

                addStmt.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public List<TastingNote> getTastingNotes(int bottleIndex) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bottles")) {
            ResultSet rs = stmt.executeQuery();
            int index = 0;
            int id = -1;

            while (rs.next()) {
                if (bottleIndex == index) {
                    id = rs.getInt("id");
                    break;
                }
                index++;
            }

            if (id == -1) {
                throw new IllegalArgumentException("Your index does not correspond to a bottle in your cellar.");
            }

            try (PreparedStatement idStmt = conn.prepareStatement("SELECT * FROM tasting_notes WHERE bottle_id = ?")) {
                idStmt.setInt(1, id);
                ResultSet rsFinal = idStmt.executeQuery();
                List<TastingNote> tastingNotes = new ArrayList<>();

                while (rsFinal.next()) {
                    String note = rsFinal.getString("note");
                    LocalDate date = rsFinal.getDate("date").toLocalDate();

                    int ratingValue = rsFinal.getInt("rating");
                    Optional<Integer> rating = rsFinal.wasNull() ? Optional.empty() : Optional.of(ratingValue);

                    tastingNotes.add(new TastingNote(note, date, rating));
                }

                return tastingNotes;
            } catch (SQLException e) {
                throw new RuntimeException();
            }

        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public void updateBottleStatus(int bottleIndex, BottleStatus status) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bottles")) {
            ResultSet rs = stmt.executeQuery();
            int index = 0;
            int id = -1;

            while (rs.next()) {
                if (bottleIndex == index) {
                    id = rs.getInt("id");
                    break;
                }
                index++;
            }

            if (id == -1) {
                throw new IllegalArgumentException("Your index does not correspond to a bottle in your cellar.");
            }

            try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE bottles SET status = ?, status_date = ?, status_price = ?, status_reason = ? WHERE id = ?")) {
                updateStmt.setInt(5, id);

                if (status instanceof BottleStatus.InCellar) {
                    updateStmt.setString(1, "IN_CELLAR");
                    updateStmt.setNull(2, Types.DATE);
                    updateStmt.setNull(3, Types.DECIMAL);
                    updateStmt.setNull(4, Types.VARCHAR);
                } else if (status instanceof BottleStatus.Consumed c) {
                    updateStmt.setString(1, "CONSUMED");
                    updateStmt.setDate(2, Date.valueOf(c.date()));
                    updateStmt.setNull(3, Types.DECIMAL);
                    updateStmt.setNull(4, Types.VARCHAR);
                } else if (status instanceof BottleStatus.Sold s) {
                    updateStmt.setString(1, "SOLD");
                    updateStmt.setDate(2, Date.valueOf(s.date()));
                    updateStmt.setBigDecimal(3, s.price());
                    updateStmt.setNull(4, Types.VARCHAR);
                } else if (status instanceof BottleStatus.Removed r) {
                    updateStmt.setString(1, "REMOVED");
                    updateStmt.setNull(2, Types.DATE);
                    updateStmt.setNull(3, Types.DECIMAL);

                    if (r.reason().isPresent()) {
                        updateStmt.setString(4, r.reason().get());
                    } else {
                        updateStmt.setNull(4, Types.VARCHAR);
                    }
                    
                }

                updateStmt.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException();
            }

        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public void updateBottleLocation(int bottleIndex, int locationId, Optional<String> bin) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bottles")) {
            ResultSet rs = stmt.executeQuery();

            int id = -1;
            int index = 0;
            
            while (rs.next()) {
                if (bottleIndex == index) {
                    id = rs.getInt("id");
                    break;
                }
                index++;
            }

            if (id == -1) {
                throw new IllegalArgumentException("Your index does not correspond to a bottle in your cellar.");
            }

            try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE bottles SET location_id = ?, bin = ? WHERE id = ?")) {
                updateStmt.setInt(1, locationId);

                if (bin.isPresent()) {
                    updateStmt.setString(2, bin.get());
                } else {
                    updateStmt.setNull(2, Types.VARCHAR);
                }

                updateStmt.setInt(3, id);

                updateStmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException();
            }
            
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

}
