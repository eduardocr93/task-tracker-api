package com.eduardo.tasktracker.controller;

import com.eduardo.tasktracker.dto.TaskRequest;
import com.eduardo.tasktracker.dto.TaskResponse;
import com.eduardo.tasktracker.entity.Task;
import com.eduardo.tasktracker.entity.TaskStatus;
import com.eduardo.tasktracker.exception.NotFoundException;
import com.eduardo.tasktracker.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository repo;

    public TaskController(TaskRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskRequest req) {
        Task t = new Task();
        t.setTitle(req.getTitle().trim());
        t.setDescription(req.getDescription());
        t.setStatus(req.getStatus() != null ? req.getStatus() : TaskStatus.TODO);
        return toResponse(repo.save(t));
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Long id) {
        Task t = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found: " + id));
        return toResponse(t);
    }

    @GetMapping
    public Page<TaskResponse> list(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        if (q != null && !q.isBlank()) {
            return repo.findByTitleContainingIgnoreCase(q.trim(), pageable).map(this::toResponse);
        }
        if (status != null) {
            return repo.findByStatus(status, pageable).map(this::toResponse);
        }
        return repo.findAll(pageable).map(this::toResponse);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest req) {
        Task t = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found: " + id));

        t.setTitle(req.getTitle().trim());
        t.setDescription(req.getDescription());
        if (req.getStatus() != null) t.setStatus(req.getStatus());

        return toResponse(repo.save(t));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Task t = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found: " + id));
        repo.delete(t);
    }

    private TaskResponse toResponse(Task t) {
        return new TaskResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}