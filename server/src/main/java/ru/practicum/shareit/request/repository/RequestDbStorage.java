package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestDbStorage extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterIdOrderByCreatedDesc(Long userId);

    List<Request> findAllByRequesterIdNot(Long userId);

}

