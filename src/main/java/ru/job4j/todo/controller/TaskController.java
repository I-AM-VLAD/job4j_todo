package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.PriorityService;
import ru.job4j.todo.service.TaskService;
import ru.job4j.todo.service.UserService;

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
        model.addAttribute("users", users);
        model.addAttribute("priorities", priorities);
        return "create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Task task, @RequestParam("userId") int userId, @RequestParam("priorityId") int priorityId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + userId + " не найден"));
        task.setUser(user);
        Priority priority = priorityService.findById(priorityId)
                .orElseThrow(() -> new IllegalArgumentException("Приоритет с ID " + priorityId + " не найден"));
        task.setPriority(priority);
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
