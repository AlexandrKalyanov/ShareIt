package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select i from Comment i where i.item.id =?1 order by i.created desc")
   Collection<Comment> findCommentByItem(Long itemId);
}