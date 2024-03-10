package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequester_Id(long userId);

    @Query(value = "select i from ItemRequest as i join fetch i.requester where i.requester.id != :userId",
            countQuery = "select count(u) FROM ItemRequest as u where u.requester.id != :userId")
    List<ItemRequest> findAllWithPage(@Param("userId") long userId, PageRequest pageRequest);

    @Query(value = "select i from ItemRequest as i join fetch i.requester where i.id = :id")
    Optional<ItemRequest> findById(@Param("id") long requestId);

}
