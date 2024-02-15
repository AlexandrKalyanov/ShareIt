package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;


public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "select i from Item i where i.id =?1 and i.owner.id =?2")
    Optional<Item> findByIdWithUser(long itemId, long userId);

    @Query(value = "select i from Item i where i.owner.id =?1 order by i.id asc")
    List<Item> findAllByUser(long userId);

    Boolean existsByOwnerId(long ownerId);

    @Query(value = "select i from Item i where (lower(i.description) like %:text% or lower(i.name) like %:text%) and i.available = true")
    List<Item> searchItems(String text);
    @Query(value = "select i from Item i where i.request.id in ?1")
    List<Item> searchByRequestIds(List<Long> listRequestsIds);

    default Item getItemOrException(Long itemId) {
        return findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
    }
}
