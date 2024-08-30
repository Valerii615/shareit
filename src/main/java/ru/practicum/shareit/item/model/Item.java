package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private String available; // — статус о том, доступна или нет вещь для аренды;
    private Long owner; // — владелец вещи;
    private String request; /*  — если вещь была создана по запросу другого пользователя,
                                то в этом поле будет храниться ссылка на соответствующий запрос.*/
}
