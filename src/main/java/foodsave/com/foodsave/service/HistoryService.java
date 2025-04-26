package foodsave.com.foodsave.service;

import foodsave.com.foodsave.model.History;
import foodsave.com.foodsave.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public List<History> getAllHistory() {
        return historyRepository.findAll();
    }

    public History save(History history) {
        return historyRepository.save(history);
    }
}
