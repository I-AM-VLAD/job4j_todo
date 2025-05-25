package ru.job4j.todo.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.repository.CategoryRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleCategoryService implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Optional> findByIds(List<Integer> ids) {
        return categoryRepository.findByIds(ids);
    }

    @Override
    public Collection<Category> findAll() {
        return categoryRepository.findAll();
    }
}
