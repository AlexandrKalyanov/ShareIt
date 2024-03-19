package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;


public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "select i from Item i where i.id =?1 and i.owner.id =?2")
    Optional<Item> findByIdWithUser(long itemId, long userId);

    @Query(value = "SELECT * FROM ITEMS WHERE ITEMS.OWNER_ID = ?1 ORDER BY ID ",
            countQuery = "SELECT count(*) FROM ITEMS WHERE OWNER_ID = ?1",
            nativeQuery = true)
    List<Item> findAllByUser(long userId, PageRequest pageRequest);

    Boolean existsByOwnerId(long ownerId);

    @Query(value = "select i from Item i where (lower(i.description) like %:text% or lower(i.name) like %:text%) and i.available = true",
            countQuery = "select count(i) from Item i where (lower(i.description) like %:text% or lower(i.name) like %:text%) and i.available = true")
    List<Item> searchItems(String text, PageRequest pageRequest);

    @Query(value = "select i from Item i where i.request.id in ?1")
    List<Item> searchByRequestIds(List<Long> listRequestsIds);

    default Item getItemOrException(Long itemId) {
        return findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
    }
}
