package ru.job4j.todo.repository;

import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);

    boolean deleteById(int id);

    boolean update(Task task);

    Collection<Task> findAll();

    Collection<Task> findCompleted();

    Collection<Task> findNew();

    Optional<Task> findById(int id);
}
