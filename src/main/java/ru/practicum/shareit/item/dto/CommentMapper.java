package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.comment.Comment;

public class CommentMapper {
    public static CommentDTO toCommentDto(Comment comment) {
        return CommentDTO.builder()
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .id(comment.getId())
                .build();

    }
}
