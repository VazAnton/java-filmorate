package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Feed {
    private int eventId;
    private Long timestamp;
    private int userId;
    private String eventType;
    private String operation;
    private int entityId;
}
