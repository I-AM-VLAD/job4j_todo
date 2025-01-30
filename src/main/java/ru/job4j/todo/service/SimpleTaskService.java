package ru.job4j.todo.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.repository.TaskRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleTaskService implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public boolean deleteById(int id) {
        return taskRepository.deleteById(id);
    }

    @Override
    public boolean update(Task task) {
        return taskRepository.update(task);
    }

    @Override
    public Collection<Task> findAll() {
        Collection<Task> tasks = taskRepository.findAll();
        return tasks != null ? tasks : Collections.emptyList();
    }

    @Override
    public Collection<Task> findCompleted() {
        Collection<Task> tasks = taskRepository.findCompleted();
        return tasks != null ? tasks : Collections.emptyList();
    }

    @Override
    public Collection<Task> findNew() {
        Collection<Task> tasks = taskRepository.findNew();
        return tasks != null ? tasks : Collections.emptyList();
    }

    @Override
    public Optional<Task> findById(int id) {
        return taskRepository.findById(id);
    }

    @Override
    public boolean doneTask(Task task) {
        return taskRepository.doneTask(task);
    }
}
