package winecellar.cli;
import java.util.Scanner;
import winecellar.model.Bottle;
import winecellar.storage.CellarRepository;
import winecellar.storage.PostgresCellarRepository;
import java.util.Comparator;
import java.util.List;

public class Main {

    public static void main(String[] args) {  
        CellarRepository myCellar = new PostgresCellarRepository("jdbc:postgresql://localhost/winecellar");

        Scanner input = new Scanner(System.in);
        boolean isRunning = true;

        System.out.println("Hello, welcome to your wine cellar!");
        System.out.println("Here are the available commands: list, add, remove, search, sort, quit");
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

                        Bottle bottle = new Bottle(producer, name, vintage, region);
                        myCellar.add(bottle);
                    } catch (NumberFormatException e) {
                        System.out.println("Vintage must be a number.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
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
