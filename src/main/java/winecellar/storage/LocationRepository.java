package winecellar.storage;
import winecellar.model.Location;
import java.util.List;

public interface LocationRepository {
    void addLocation(Location location);

    List<Location> allLocations();

    void removeLocation(int index);

    int getLocationId(int index);
}
