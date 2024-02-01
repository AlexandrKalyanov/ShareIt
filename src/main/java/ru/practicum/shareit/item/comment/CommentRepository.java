package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select i from Comment i where i.item.id =?1 order by i.created desc")
    Collection<Comment> findCommentByItem(Long itemId);

    @Query("select i from Comment i where i.item.id in ?1 order by i.created desc")
    Collection<Comment> findAllCommentsInListItemsIds(List<Long> itemsIds);
}
