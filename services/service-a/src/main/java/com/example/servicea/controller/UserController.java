package com.example.servicea.controller;

import com.example.servicea.model.AppUser;
import com.example.servicea.repository.AppUserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private final AppUserRepository repo;

    public UserController(AppUserRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/users")
    public List<AppUser> list() {
        return repo.findAll();
    }

    @PostMapping("/users")
    public AppUser create(@RequestBody AppUser u) {
        return repo.save(u);
    }
}