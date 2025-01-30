package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.service.TaskService;

import java.util.*;
import java.util.function.Supplier;

@ThreadSafe
@Controller
@AllArgsConstructor
@RequestMapping
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public String getTasks(@RequestParam(value = "filter", required = false) String filter, Model model) {
        Collection<Task> tasks;

        Map<String, Supplier<Collection<Task>>> filterMap = Map.of(
                "completed", taskService::findCompleted,
                "new", taskService::findNew
        );

        Supplier<Collection<Task>> supplier = filterMap.getOrDefault(filter, taskService::findAll);
        Collection<Task> result = (supplier != null) ? supplier.get() : Collections.emptyList();
        tasks = result;

        model.addAttribute("tasks", tasks);
        model.addAttribute("filter", filter);
        return "list";
    }

    @GetMapping("/create")
    public String getCreationPage() {
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
