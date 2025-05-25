package ru.job4j.todo.service;

import ru.job4j.todo.model.Category;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Optional> findByIds(List<Integer> ids);

    Collection<Category> findAll();
}
