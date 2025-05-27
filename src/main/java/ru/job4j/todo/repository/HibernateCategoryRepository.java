package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Category;

import java.util.*;

@Repository
@AllArgsConstructor
public class HibernateCategoryRepository implements CategoryRepository {

    private final CrudRepository crudRepository;

    @Override
    public List<Optional> findByIds(List<Integer> ids) {
        List<Optional> result = new ArrayList<>();
        for (Integer id : ids) {
            result.add(
                    crudRepository.optional("from Category as c where c.id = :id ", Category.class,
                            Map.of("id", id))
            );
        }
        return result;
    }

    @Override
    public Collection<Category> findAll() {
        return crudRepository.query("from Category", Category.class);
    }

}
