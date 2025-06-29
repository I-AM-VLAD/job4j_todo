package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;

import java.util.*;

@Repository
@AllArgsConstructor
public class HibernateUserRepository implements UserRepository {

    private final CrudRepository crudRepository;

    @Override
    public User save(User user) {
        crudRepository.run(session -> session.persist(user));
        return user;
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        return crudRepository.optional(
                "from User as u where u.login = :login and u.password = :password", User.class,
                Map.of("login", login,
                        "password", password
                )
        );
    }

    @Override
    public Optional<User> findById(int id) {
        return crudRepository.optional(
                "from User as u where u.id = :id", User.class,
                Map.of("id", id)
        );
    }

    @Override
    public Collection<User> findAll() {
        return crudRepository.query("from User", User.class);
    }


}