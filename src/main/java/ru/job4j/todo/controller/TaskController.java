package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.service.TaskService;

import java.util.Optional;

@ThreadSafe
@Controller
@AllArgsConstructor
@RequestMapping
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "list";
    }

    @GetMapping("/completed")
    public String getCompleted(Model model) {
        model.addAttribute("completed", taskService.findCompleted());
        return "completed";
    }

    @GetMapping("/news")
    public String getNew(Model model) {
        model.addAttribute("news", taskService.findNew());
        return "news";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model) {
        model.addAttribute("task", new Task());
        return "create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Task task) {
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
        try {
            var isUpdated = taskService.update(task);
            if (!isUpdated) {
                model.addAttribute("message", "Задания с указанным идентификатором не найдено");
                return "404";
            }
            return "redirect:/";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "404";
        }
    }
}
