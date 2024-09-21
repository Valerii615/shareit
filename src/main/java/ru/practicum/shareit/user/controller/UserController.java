package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        return userMapper.toUserDto(userService.createUser(userDto));
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userMapper.toUserDto(userService.findUserById(id));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        return userMapper.toUserDto(userService.updateUser(id, userDto));
    }
}