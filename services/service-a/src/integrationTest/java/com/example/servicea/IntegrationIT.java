package com.example.servicea;

import com.example.servicea.model.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class IntegrationIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("healthdb")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void jdbcProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void contextLoads() {
        // Smoke test — if Spring context starts successfully the test passes.
    }

    @Test
    void listUsersReturnsInitialData() {
        AppUser[] users = restTemplate.getForObject("/users", AppUser[].class);
        assertThat(users).isNotNull().hasLength(2);
        assertThat(users[0].getName()).isIn("Alice", "Bob");
        assertThat(users[1].getName()).isIn("Alice", "Bob");
    }

    @Test
    void createUserSuccessfully() {
        AppUser newUser = new AppUser("Charlie");
        ResponseEntity<AppUser> response = restTemplate.postForEntity("/users", newUser, AppUser.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Charlie");
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void createUserWithBlankNameReturnsValidationError() {
        AppUser invalidUser = new AppUser();
        invalidUser.setName("");  // blank name
        
        ResponseEntity<String> response = restTemplate.postForEntity("/users", invalidUser, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Validation Failed").contains("fields");
    }

    @Test
    void createUserWithNullNameReturnsValidationError() {
        AppUser invalidUser = new AppUser();
        invalidUser.setName(null);  // null name
        
        ResponseEntity<String> response = restTemplate.postForEntity("/users", invalidUser, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void actuatorHealthEndpointIsAvailable() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("UP");
    }
}
