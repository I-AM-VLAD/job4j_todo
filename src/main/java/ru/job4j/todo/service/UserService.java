package ru.job4j.todo.service;

import ru.job4j.todo.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {
    User save(User user);

    Optional<User> findByLoginAndPassword(String login, String password);

    Optional<User> findById(int id);

    Collection<User> findAll();

}
