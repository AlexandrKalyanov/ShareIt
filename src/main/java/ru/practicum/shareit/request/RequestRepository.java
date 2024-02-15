package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequester_Id(long userId);

    @Query(value = "SELECT * FROM ITEM_REQUEST WHERE REQUESTER_ID != ?1",
            countQuery = "SELECT count(*) FROM ITEM_REQUEST WHERE REQUESTER_ID != ?1",
            nativeQuery = true)
    List<ItemRequest> findAllWithPage(long userId, PageRequest pageRequest);
}
