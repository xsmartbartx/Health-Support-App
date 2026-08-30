package com.example.servicea;

import com.example.servicea.model.AppUser;
import com.example.servicea.repository.AppointmentRepository;
import com.example.servicea.repository.AppUserRepository;
import com.example.servicea.repository.MedicationRepository;
import com.example.servicea.repository.PatientRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class IntegrationIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AppUserRepository repository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private MedicationRepository medicationRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void resetDatabase() {
        // All tests share one PostgreSQL container and Spring context, so reset the
        // seeded rows before each test to keep them order-independent. Children first
        // to respect foreign keys.
        appointmentRepository.deleteAll();
        medicationRepository.deleteAll();
        patientRepository.deleteAll();
        repository.deleteAll();
        repository.save(new AppUser("Alice"));
        repository.save(new AppUser("Bob"));
    }

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
    void listUsersReturnsInitialData() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("/users", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode content = objectMapper.readTree(response.getBody()).path("content");
        assertThat(content.size()).isEqualTo(2);
        assertThat(content.findValuesAsText("name")).containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void createUserSuccessfully() throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity("/users", Map.of("name", "Charlie"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.path("name").asText()).isEqualTo("Charlie");
        assertThat(body.path("id").isNumber()).isTrue();
    }

    @Test
    void createUserWithBlankNameReturnsValidationError() {
        ResponseEntity<String> response = restTemplate.postForEntity("/users", Map.of("name", ""), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Validation Failed").contains("fields");
    }

    @Test
    void createUserWithNullNameReturnsValidationError() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/users", Collections.singletonMap("name", null), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void actuatorHealthEndpointIsAvailable() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("UP");
    }
}
