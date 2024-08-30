package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long item; // — вещь, которую пользователь бронирует;
    private Long booker; // — пользователь, который осуществляет бронирование;
    private String status; /* — статус бронирования. Может принимать одно из следующих
                            значений: WAITING — новое бронирование, ожидает одобрения, APPROVED —
                            Дополнительные советы ментора 2
                            бронирование подтверждено владельцем, REJECTED — бронирование
                            отклонено владельцем, CANCELED — бронирование отменено создателем. */
}
