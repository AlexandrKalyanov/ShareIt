package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "SELECT * FROM ITEMS WHERE ID = ?1 AND OWNER_ID = ?2", nativeQuery = true)
    Optional<Item> findByIdWithUser(long itemId, long userId);

    @Query(value = "SELECT * FROM ITEMS WHERE OWNER_ID = ?", nativeQuery = true)
    List<Item> findAllByUser(long userId);

    @Query(value = "SELECT i FROM Item i WHERE (lower(i.description) LIKE %:text% OR lower(i.name) LIKE %:text%) and i.available = true")
    List<Item> searchItems(String text);
}
