package com.example.servicea;

import com.example.servicea.model.AppUser;
import com.example.servicea.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class DataInit {

    @Bean
    @Lazy
    CommandLineRunner init(AppUserRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new AppUser("Alice"));
                repo.save(new AppUser("Bob"));
            }
        };
    }
}