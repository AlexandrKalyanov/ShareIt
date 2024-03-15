package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toCommentDto() {
        User user = new User(1L, "Name", "mail@mail.ru");
        Item item = new Item(1L, "Sample", "new Sample", true, user, null);
        Comment comment = new Comment(1L, "text", item, user, LocalDateTime.of(2024, 12, 12, 12, 12));
        CommentDTO commentDTO = new CommentDTO(1L, "text", user.getName(), LocalDateTime.of(2024, 12, 12, 12, 12));

        assertEquals(commentDTO, CommentMapper.toCommentDto(comment));
    }
}