package foodsave.com.foodsave.service;

import foodsave.com.foodsave.model.Store;
import foodsave.com.foodsave.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    // Получение списка всех магазинов
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    // Получение магазина по ID
    public Store findById(Long id) {
        return storeRepository.findById(id).orElseThrow(() -> new RuntimeException("Store not found"));
    }

    // Сохранение нового магазина или обновление существующего
    public Store saveStore(Store store) {
        return storeRepository.save(store);
    }

    // Обновление данных магазина
    public Store updateStore(Long id, Store updatedStore) {
        Store existingStore = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        existingStore.setName(updatedStore.getName());
        existingStore.setType(updatedStore.getType());
        existingStore.setCategory(updatedStore.getCategory());
        existingStore.setLocation(updatedStore.getLocation());
        existingStore.setContactInfo(updatedStore.getContactInfo());

        return storeRepository.save(existingStore);
    }

    // Удаление магазина
    public void deleteStore(Long id) {
        storeRepository.deleteById(id);
    }
    public List<Store> searchStores(String query) {
        return storeRepository.findByNameContaining(query);
    }

}
