package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.comment.Comment;

@UtilityClass
public final class CommentMapper {
    public CommentDTO toCommentDto(Comment comment) {
        return CommentDTO.builder()
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .id(comment.getId())
                .build();

    }
}
