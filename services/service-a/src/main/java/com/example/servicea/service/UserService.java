package com.example.servicea.service;

import com.example.servicea.dto.UserRequest;
import com.example.servicea.dto.UserResponse;
import com.example.servicea.model.AppUser;
import com.example.servicea.repository.AppUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final AppUserRepository repository;

    public UserService(AppUserRepository repository) {
        this.repository = repository;
    }

    public Page<UserResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(UserResponse::from);
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        return UserResponse.from(repository.save(new AppUser(request.getName())));
    }
}
