package com.eduardo.tasktracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    @GetMapping("/health")
    public String health() {
        return "Task Tracker API is running";
    }
}