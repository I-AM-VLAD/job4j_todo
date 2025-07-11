package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.CategoryService;
import ru.job4j.todo.service.PriorityService;
import ru.job4j.todo.service.TaskService;
import ru.job4j.todo.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Supplier;

@ThreadSafe
@Controller
@AllArgsConstructor
@RequestMapping
public class TaskController {

    private final TaskService taskService;

    private final UserService userService;

    private final PriorityService priorityService;

    private final CategoryService categoryService;

    @GetMapping
    public String getTasks(@RequestParam(value = "filter", required = false) String filter, Model model) {
        Collection<Task> tasks;

        if ("completed".equals(filter)) {
            tasks = taskService.findCompleted();
        } else if ("new".equals(filter)) {
            tasks = taskService.findNew();
        } else {
            tasks = taskService.findAll();
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("filter", filter);
        return "list";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model) {
        Collection<User> users = userService.findAll();
        Collection<Priority> priorities = priorityService.findAll();
        Collection<Category> categories = categoryService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("priorities", priorities);
        model.addAttribute("categories", categories);
        return "create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Task task,
                         @RequestParam("categoryIds") List<Integer> categoryIds, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("Пользователь не авторизован");
        }

        List<Optional> categoriesOptional = categoryService.findByIds(categoryIds);
        List<Category> categories = new ArrayList<>();

        for (Optional one : categoriesOptional) {
            if (one.isPresent()) {
                categories.add((Category) one.get());
            }
        }

        task.setUser(user);
        task.setCategories(categories);
        taskService.save(task);
        return "redirect:/";
    }

    @GetMapping("/list/{id}")
    public String getTaskDetails(@PathVariable int id, Model model) {
        Optional<Task> optionalTask = taskService.findById(id);

        if (optionalTask.isPresent()) {
            model.addAttribute("task", optionalTask.get());
            return "update";
        } else {
            model.addAttribute("message", "Задача с ID " + id + " не найдена");
            return "404";
        }
    }


    @PostMapping("/list/{id}/delete")
    public String delete(@PathVariable int id, Model model) {
        var isDeleted = taskService.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Задания с указанным идентификатором не найдено");
            return "404";
        }
        return "redirect:/";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Task task, Model model) {
            var isUpdated = taskService.update(task);
            if (!isUpdated) {
                model.addAttribute("message", "Задания с указанным идентификатором не найдено");
                return "404";
            }
            return "redirect:/";
    }

    @PostMapping("/update/doneTask")
    public String doneTask(@RequestParam("id") int id, Model model) {
        Optional<Task> optionalTask = taskService.findById(id);

        if (optionalTask.isEmpty()) {
            model.addAttribute("message", "Задание с указанным идентификатором не найдено");
            return "404";
        }

        Task task = optionalTask.get();
        task.setDone(true);

        boolean isDone = taskService.doneTask(task);
        if (!isDone) {
            model.addAttribute("message", "Не удалось обновить задание");
            return "404";
        }
        return "redirect:/";
    }


}
