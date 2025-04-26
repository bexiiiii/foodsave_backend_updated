package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.model.History;
import foodsave.com.foodsave.service.HistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public List<History> getAllHistory() {
        return historyService.getAllHistory();
    }

    // опционально: POST для добавления записи
    @PostMapping
    public History addHistory(@RequestBody History history) {
        return historyService.save(history);
    }
}
