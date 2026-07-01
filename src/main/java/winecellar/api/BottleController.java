package winecellar.api;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import winecellar.model.Bottle;
import winecellar.storage.CellarRepository;
import org.springframework.http.ResponseEntity;

@RestController
public class BottleController {

    private final CellarRepository cellar;

    public BottleController(CellarRepository cellar) {
        this.cellar = cellar;
    }

    @GetMapping("/bottles")
    public List<Bottle> getBottles() {
        return cellar.allBottles();
    }

    @PostMapping("/bottles")
    public Bottle addBottle(@RequestBody Bottle bottle) {
        cellar.add(bottle);
        return bottle;
    }

    @DeleteMapping("/bottles/{index}")
    public void removeBottle(@PathVariable int index) {
        cellar.remove(index);
    }

    @GetMapping("/bottles/search")
    public List<Bottle> getBottlesByProducer(@RequestParam String producer) {
        return cellar.findByProducer(producer);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
