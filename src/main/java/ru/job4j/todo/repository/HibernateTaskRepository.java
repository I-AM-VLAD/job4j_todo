package ru.job4j.todo.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

@Repository
@AllArgsConstructor
public class HibernateTaskRepository implements TaskRepository {

    private final CrudRepository crudRepository;

    @Override
    public Task save(Task task) {
        crudRepository.run(session -> session.persist(task));
        return task;
    }


    @Override
    public boolean deleteById(int id) {
        return crudRepository.executeUpdate("delete from Task where id = :id",
                Map.of("id", id));
    }

    @Override
    public boolean update(Task task) {
        return crudRepository.executeUpdate("UPDATE Task t SET t.title = :title, t.description = :description WHERE t.id = :id",
                Map.of("title", task.getTitle(),
                        "description", task.getDescription(),
        "id", task.getId()));
    }


    @Override
    public Collection<Task> findAll() {
        return crudRepository.query("FROM Task t JOIN FETCH t.priority", Task.class);
    }

    @Override
    public Collection<Task> findCompleted() {
       return findTasks(true);
    }

    @Override
    public Collection<Task> findNew() {
        return findTasks(false);
    }

    public Collection<Task> findTasks(boolean bool) {
        return crudRepository.query(
                "from Task as t where t.done = :done", Task.class,
                Map.of("done", bool)
        );
    }

    @Override
    public Optional<Task> findById(int id) {
        return crudRepository.optional(
                "from Task as t where t.id = :id", Task.class,
                Map.of("id", id)
        );
    }

    @Override
    public boolean doneTask(Task task) {
        return crudRepository.executeUpdate("UPDATE Task t SET t.done = :done WHERE t.id = :id",
                Map.of("done", true,
                        "id", task.getId()));
    }
}
