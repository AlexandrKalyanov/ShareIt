package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.model.User;


import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "item_request")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "description")
    private String description;
    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;
    @Column(name = "created")
    private LocalDateTime created;

}
