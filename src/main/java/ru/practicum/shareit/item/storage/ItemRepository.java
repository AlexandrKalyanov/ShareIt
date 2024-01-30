package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;


public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "select i from Item i where i.id =?1 and i.owner.id =?2")
    Optional<Item> findByIdWithUser(long itemId, long userId);

    @Query(value = "select i from Item i where i.owner.id =?1")
    List<Item> findAllByUser(long userId);

    @Query(value = "select i from Item i where (lower(i.description) like %:text% or lower(i.name) like %:text%) and i.available = true")
    List<Item> searchItems(String text);
}
