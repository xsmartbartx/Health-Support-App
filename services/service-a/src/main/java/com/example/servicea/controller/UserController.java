package com.example.servicea.controller;

import com.example.servicea.model.AppUser;
import com.example.servicea.repository.AppUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Users", description = "User management API")
public class UserController {

    private final AppUserRepository repo;

    public UserController(AppUserRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/users")
    @Operation(summary = "List all users")
    public List<AppUser> list() {
        return repo.findAll();
    }

    @PostMapping("/users")
    @Operation(summary = "Create a new user")
    public AppUser create(@Valid @RequestBody AppUser u) {
        return repo.save(u);
    }
}
