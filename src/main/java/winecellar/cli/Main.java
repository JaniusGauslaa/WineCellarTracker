package winecellar.cli;
import java.util.Scanner;
import winecellar.model.Bottle;
import winecellar.model.TastingNote;
import winecellar.model.WineType;
import winecellar.model.BottleStatus;
import winecellar.model.Location;
import winecellar.storage.CellarRepository;
import winecellar.storage.LocationRepository;
import winecellar.storage.PostgresCellarRepository;
import winecellar.storage.PostgresLocationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {  
        CellarRepository myCellar = new PostgresCellarRepository("jdbc:postgresql://localhost/winecellar");
        LocationRepository myLocations = new PostgresLocationRepository("jdbc:postgresql://localhost/winecellar");

        Scanner input = new Scanner(System.in);
        boolean isRunning = true;

        System.out.println("Hello, welcome to your wine cellar!");
        System.out.println("Here are the available commands: list, add, remove, search, sort, quit, add-note, view-notes, update-status, add-location, view-locations, assign-location");
        System.out.println("");

        while (isRunning) {
            System.out.print("> ");
            String command = input.nextLine();
            
            switch (command) {
                case "list":
                    if (myCellar.allBottles().isEmpty()) {
                        System.out.println("Your cellar is currently empty.");
                    } else {
                        for (Bottle bottle : myCellar.allBottles()) {
                            System.out.println(bottle);
                        }
                    }
                    break;
                case "add":
                    try {
                        System.out.print("Producer: ");
                        String producer = input.nextLine();

                        System.out.print("Name: ");
                        String name = input.nextLine();

                        System.out.print("Vintage: ");
                        int vintage = Integer.parseInt(input.nextLine());

                        System.out.print("Region: ");
                        String region = input.nextLine();

                        System.out.print("Type: ");
                        WineType type = WineType.valueOf(input.nextLine().toUpperCase());

                        System.out.print("Rating (1-100), press enter to skip: ");
                        String ratingString = input.nextLine();
                        Optional<Integer> rating = ratingString.isBlank() ? Optional.empty() : Optional.of(Integer.parseInt(ratingString));

                        System.out.print("Ready Year, press enter to skip: ");
                        String readyYearString = input.nextLine();
                        Optional<Integer> readyYear = readyYearString.isBlank() ? Optional.empty() : Optional.of(Integer.parseInt(readyYearString));

                        System.out.print("Peak Year, press enter to skip: ");
                        String peakYearString = input.nextLine();
                        Optional<Integer> peakYear = peakYearString.isBlank() ? Optional.empty() : Optional.of(Integer.parseInt(peakYearString));

                        System.out.print("Price, press enter to skip: ");
                        String priceString = input.nextLine();
                        Optional<BigDecimal> price = priceString.isBlank() ? Optional.empty() : Optional.of(new BigDecimal(priceString));

                        System.out.print("Purchase date (YYYY-MM-DD), press enter to skip: ");
                        String purchaseDateString = input.nextLine();
                        Optional<LocalDate> purchaseDate = purchaseDateString.isBlank() ? Optional.empty() : Optional.of(LocalDate.parse(purchaseDateString));

                        System.out.print("Store, press enter to skip: ");
                        String storeString = input.nextLine();
                        Optional<String> store = storeString.isBlank() ? Optional.empty() : Optional.of(storeString);

                        Bottle bottle = new Bottle(producer, name, vintage, region, type, rating, readyYear, peakYear, price, purchaseDate, store, new BottleStatus.InCellar(), Optional.empty(), Optional.empty());
                        myCellar.add(bottle);
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format. Use YYYY-MM-DD.");
                    }

                    break;
                case "search":
                    System.out.print("Search by (producer/region/vintage/name): ");
                    String searchType = input.nextLine();

                    switch (searchType) {
                        case "producer": {
                            System.out.print("Producer: ");
                            String searchTerm = input.nextLine();

                            List<Bottle> matchedBottles = myCellar.findBy(b -> b.producer().equals(searchTerm));

                            if (matchedBottles.isEmpty()) {
                                System.out.println("No bottles in your cellar made by that producer.");
                                break;
                            }

                            System.out.println("Bottles that match your search:");
                            for (Bottle bottle : matchedBottles) {
                                System.out.println(bottle);
                            }

                            break;
                        }
                        case "region": {
                            System.out.print("Region: ");
                            String searchTerm = input.nextLine();

                            List<Bottle> matchedBottles = myCellar.findBy(b -> b.region().equals(searchTerm));

                            if (matchedBottles.isEmpty()) {
                                System.out.println("No bottles in your cellar from that region.");
                                break;
                            }

                            System.out.println("Bottles that match your search:");
                            for (Bottle bottle : matchedBottles) {
                                System.out.println(bottle);
                            }

                            break;
                        }
                        case "vintage": {
                            System.out.print("Vintage: ");
                            String searchTerm = input.nextLine();

                            try {
                                List<Bottle> matchedBottles = myCellar.findBy(b -> b.vintage() == Integer.parseInt(searchTerm));

                                if (matchedBottles.isEmpty()) {
                                    System.out.println("No bottles in your cellar from that vintage.");
                                    break;
                                }

                                System.out.println("Bottles that match your search:");
                                for (Bottle bottle : matchedBottles) {
                                    System.out.println(bottle);
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Vintage must be a number");
                            }

                            break;
                        }
                        case "name": {
                            System.out.print("Name: ");
                            String searchTerm = input.nextLine();

                            List<Bottle> matchedBottles = myCellar.findBy(b -> b.name().equals(searchTerm));

                            if (matchedBottles.isEmpty()) {
                                System.out.println("No bottles in your cellar with that name.");
                                break;
                            }

                            System.out.println("Bottles that match your search:");
                            for (Bottle bottle : matchedBottles) {
                                System.out.println(bottle);
                            }

                            break;
                        }
                        case null, default: {
                            System.out.println("Unknown search type");
                            break;
                        }
                    }
                    break;
                case "remove":
                    try {
                        if (myCellar.allBottles().isEmpty()) {
                            System.out.println("Your cellar is empty.");
                            break;
                        }

                        List<Bottle> myBottles = myCellar.allBottles();
                        for (int i = 0; i < myBottles.size(); i++) {
                            System.out.println(i + 1 + ". " + myBottles.get(i));
                        }

                        System.out.print("Remove bottle #: ");
                        String bottleNumber = input.nextLine();

                        int bottleIndex = Integer.parseInt(bottleNumber) - 1;
                        myCellar.remove(bottleIndex);
                    } catch (NumberFormatException e) {
                        System.out.println("Enter a valid number.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "sort":
                    System.out.print("Sort by (producer/region/vintage/name): ");
                    String sortType = input.nextLine();

                    Comparator<Bottle> comparator = switch (sortType) {
                        case "producer" -> Comparator.comparing(Bottle::producer);
                        case "region" -> Comparator.comparing(Bottle::region);
                        case "vintage" -> Comparator.comparing(Bottle::vintage);
                        case "name" -> Comparator.comparing(Bottle::name);
                        default -> null;
                    };

                    if (comparator == null) {
                        System.out.println("Enter a valid sort type.");
                        break;
                    }

                    List<Bottle> sortedBottles = myCellar.sorted(comparator);

                    if (sortedBottles.isEmpty()) {
                        System.out.println("Your cellar is empty.");
                        break;
                    }

                    System.out.println("Here are your bottles sorted by " + sortType + ":");
                    for (Bottle bottle : sortedBottles) {
                        System.out.println(bottle);
                    }
                    break;
                case "add-note":
                    try {
                        if (myCellar.allBottles().isEmpty()) {
                            System.out.println("Your cellar is empty.");
                            break;
                        }

                        List<Bottle> myBottles = myCellar.allBottles();
                        for (int i = 0; i < myBottles.size(); i++) {
                            System.out.println(i + 1 + ". " + myBottles.get(i));
                        }

                        System.out.print("Add note to bottle #: ");
                        String bottleNumber = input.nextLine();
                        int bottleIndex = Integer.parseInt(bottleNumber) - 1;

                        System.out.print("Type note: ");
                        String note = input.nextLine();

                        LocalDate date = LocalDate.now();

                        System.out.print("Rating for this tasting (1-100), press enter to skip: ");
                        String ratingString = input.nextLine();
                        Optional<Integer> rating = ratingString.isBlank() ? Optional.empty() : Optional.of(Integer.valueOf(ratingString));

                        TastingNote tastingNote = new TastingNote(note, date, rating);
                        myCellar.addTastingNote(bottleIndex, tastingNote);
                    } catch (NumberFormatException e) {
                        System.out.println("Enter a valid number.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "view-notes":
                    try {
                        if (myCellar.allBottles().isEmpty()) {
                            System.out.println("Your cellar is empty.");
                            break;
                        }

                        List<Bottle> myBottles = myCellar.allBottles();
                        for (int i = 0; i < myBottles.size(); i++) {
                            System.out.println(i + 1 + ". " + myBottles.get(i));
                        }

                        System.out.print("View notes for bottle #: ");
                        String bottleNumber = input.nextLine();
                        int bottleIndex = Integer.parseInt(bottleNumber) - 1;

                        List<TastingNote> tastingNotes = myCellar.getTastingNotes(bottleIndex);

                        if (tastingNotes.isEmpty()) {
                            System.out.println("No tasting notes for this bottle.");
                            break;
                        }

                        for (TastingNote tn : tastingNotes) {
                            if (tn.rating().isPresent()) {
                                System.out.println(tn.date() + ": " + tn.note() + " Rating: " + tn.rating().get());
                            } else {
                                System.out.println(tn.date() + ": " + tn.note() + " - No rating.");
                            }
                            
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Enter a valid number.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "update-status":
                    try {
                        if (myCellar.allBottles().isEmpty()) {
                            System.out.println("Your cellar is empty.");
                            break;
                        }

                        List<Bottle> myBottles = myCellar.allBottles();
                        for (int i = 0; i < myBottles.size(); i++) {
                            System.out.println(i + 1 + ". " + myBottles.get(i));
                        }

                        System.out.print("Update status for bottle #: ");
                        String bottleNumber = input.nextLine();
                        int bottleIndex = Integer.parseInt(bottleNumber) - 1;

                        System.out.print("What would you like to update the status to (in-cellar, consumed, sold, removed): ");
                        String statusInput = input.nextLine();

                        BottleStatus status;
                        String dateString;
                        LocalDate date;
                        String priceString;
                        BigDecimal price;
                        String reasonString;
                        Optional<String> reason;

                        switch (statusInput) {
                            case "in-cellar":
                                status = new BottleStatus.InCellar();

                                break;
                            case "consumed":
                                System.out.print("Consumed date (YYYY-MM-DD): ");
                                dateString = input.nextLine();
                                date = LocalDate.parse(dateString);

                                status = new BottleStatus.Consumed(date);

                                break;
                            case "sold":
                                System.out.print("Sold date (YYYY-MM-DD): ");
                                dateString = input.nextLine();
                                date = LocalDate.parse(dateString);

                                System.out.print("Sold price (239.99): ");
                                priceString = input.nextLine();
                                price = new BigDecimal(priceString);

                                status = new BottleStatus.Sold(price, date);

                                break;
                            case "removed":
                                System.out.print("Reason for removal, press enter to skip: ");
                                reasonString = input.nextLine();

                                reason = reasonString.isBlank() ? Optional.empty() : Optional.of(reasonString);

                                status = new BottleStatus.Removed(reason);

                                break;
                            default:
                                throw new IllegalArgumentException("Unknown status: " + statusInput);
                        }

                        myCellar.updateBottleStatus(bottleIndex, status);

                    } catch (NumberFormatException e) {
                        System.out.println("Enter a valid number.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    } catch (DateTimeParseException e) {
                        System.out.println("Please input date in valid format (YYYY-MM-DD).");
                    }
                    break;
                case "add-location":
                    try {
                        System.out.print("Name for location: ");
                        String name = input.nextLine();

                        System.out.print("Description for location, press enter to skip: ");
                        String descriptionValue = input.nextLine();

                        Optional<String> description = descriptionValue.isBlank() ? Optional.empty() : Optional.of(descriptionValue);

                        Location location = new Location(name, description);
                        myLocations.addLocation(location);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "view-locations":
                    try {
                        if (myLocations.allLocations().isEmpty()) {
                            System.out.println("You have no locations yet.");
                            break;
                        }

                        List<Location> locations = myLocations.allLocations();
                        for (Location l : locations) {
                            System.out.println(l);
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "assign-location":
                    try {
                        if (myCellar.allBottles().isEmpty()) {
                            System.out.println("Your cellar is empty.");
                            break;
                        }

                        List<Bottle> myBottles = myCellar.allBottles();
                        for (int i = 0; i < myBottles.size(); i++) {
                            System.out.println(i + 1 + ". " + myBottles.get(i));
                        }

                        System.out.print("Assign location to bottle #: ");
                        String bottleNumber = input.nextLine();

                        int bottleIndex = Integer.parseInt(bottleNumber) - 1;

                        try {
                            if (myLocations.allLocations().isEmpty()) {
                                System.out.println("You have no locations yet.");
                                break;
                            }

                            List<Location> locations = myLocations.allLocations();
                            for (int i = 0; i < locations.size(); i++) {
                                System.out.println(i + 1 + ". " + locations.get(i));
                            }

                            System.out.print("Which location would you like to assign to this bottle #: ");
                            String locationNumber = input.nextLine();

                            int locationIndex = Integer.parseInt(locationNumber) - 1;

                            try {
                                System.out.print("Place within location (ex. Rack 3, Shelf 2), press enter to skip: ");
                                String binValue = input.nextLine();

                                Optional<String> bin = binValue.isBlank() ? Optional.empty() : Optional.of(binValue);

                                int locationId = myLocations.getLocationId(locationIndex);

                                myCellar.updateBottleLocation(bottleIndex, locationId, bin);
                            } catch (IllegalArgumentException e) {
                                System.out.println(e.getMessage());
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "quit":
                    isRunning = false;
                    break;
                case null, default:
                    System.out.println("Unknown command");
                    break;
            }
        }

        System.out.println("Thanks for using this wine cellar tracker");
    }

}
