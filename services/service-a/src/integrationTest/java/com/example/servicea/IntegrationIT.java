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
    void createPatientSuccessfully() throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity("/patients", patientBody("John", "Doe"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.path("id").isNumber()).isTrue();
        assertThat(body.path("firstName").asText()).isEqualTo("John");
        assertThat(body.path("lastName").asText()).isEqualTo("Doe");
        assertThat(body.path("email").asText()).isEqualTo("john.doe@example.com");
        assertThat(body.path("dateOfBirth").asText()).isEqualTo("1990-05-20");
    }

    @Test
    void listPatientsReturnsCreatedPatient() throws Exception {
        createPatient("Jane", "Roe");

        ResponseEntity<String> response = restTemplate.getForEntity("/patients", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode content = objectMapper.readTree(response.getBody()).path("content");
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).path("email").asText()).isEqualTo("jane.roe@example.com");
    }

    @Test
    void getPatientById() throws Exception {
        long id = createPatient("John", "Doe");

        ResponseEntity<String> response = restTemplate.getForEntity("/patients/" + id, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.path("id").asLong()).isEqualTo(id);
        assertThat(body.path("firstName").asText()).isEqualTo("John");
    }

    @Test
    void updatePatient() throws Exception {
        long id = createPatient("John", "Doe");

        Map<String, Object> update = patientBody("Jonathan", "Doe");
        ResponseEntity<String> response = restTemplate.exchange(
                "/patients/" + id, HttpMethod.PUT, jsonEntity(update), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.path("firstName").asText()).isEqualTo("Jonathan");
    }

    @Test
    void deletePatient() throws Exception {
        long id = createPatient("John", "Doe");

        ResponseEntity<Void> delete = restTemplate.exchange(
                "/patients/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertThat(delete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> get = restTemplate.getForEntity("/patients/" + id, String.class);
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getMissingPatientReturns404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/patients/999999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createPatientWithBlankFirstNameReturnsValidationError() {
        Map<String, Object> body = patientBody("", "Doe");
        ResponseEntity<String> response = restTemplate.postForEntity("/patients", body, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Validation Failed").contains("fields");
    }

    @Test
    void searchPatientsByPartialNameOrEmail() throws Exception {
        createPatient("John", "Doe");
        createPatient("Jane", "Roe");

        ResponseEntity<String> byName = restTemplate.getForEntity("/patients/search?q=john", String.class);
        assertThat(byName.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode nameResults = objectMapper.readTree(byName.getBody());
        assertThat(nameResults.size()).isEqualTo(1);
        assertThat(nameResults.get(0).path("firstName").asText()).isEqualTo("John");

        ResponseEntity<String> byEmail = restTemplate.getForEntity(
                "/patients/search?q=jane.roe@example.com", String.class);
        assertThat(objectMapper.readTree(byEmail.getBody()).size()).isEqualTo(1);
    }

    @Test
    void getPatientWithNonNumericIdReturns400() {
        ResponseEntity<String> response = restTemplate.getForEntity("/patients/not-a-number", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createPatientWithDuplicateEmailReturns409() throws Exception {
        createPatient("John", "Doe");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/patients", patientBody("John", "Doe"), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void appointmentLifecycle() throws Exception {
        long patientId = createPatient("John", "Doe");

        ResponseEntity<String> create = restTemplate.postForEntity(
                "/appointments", appointmentBody(patientId, "2030-01-01T10:00:00Z", "Annual checkup"), String.class);
        assertThat(create.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode created = objectMapper.readTree(create.getBody());
        long id = created.path("id").asLong();
        assertThat(created.path("patientName").asText()).isEqualTo("John Doe");
        assertThat(created.path("reason").asText()).isEqualTo("Annual checkup");
        assertThat(created.path("status").asText()).isEqualTo("SCHEDULED");

        ResponseEntity<String> list = restTemplate.getForEntity("/appointments", String.class);
        assertThat(objectMapper.readTree(list.getBody()).path("content").size()).isEqualTo(1);

        ResponseEntity<String> get = restTemplate.getForEntity("/appointments/" + id, String.class);
        assertThat(objectMapper.readTree(get.getBody()).path("id").asLong()).isEqualTo(id);

        Map<String, Object> update = appointmentBody(patientId, "2030-01-01T10:00:00Z", "Follow-up");
        update.put("status", "COMPLETED");
        ResponseEntity<String> put = restTemplate.exchange(
                "/appointments/" + id, HttpMethod.PUT, jsonEntity(update), String.class);
        JsonNode updated = objectMapper.readTree(put.getBody());
        assertThat(updated.path("reason").asText()).isEqualTo("Follow-up");
        assertThat(updated.path("status").asText()).isEqualTo("COMPLETED");

        ResponseEntity<Void> delete = restTemplate.exchange(
                "/appointments/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertThat(delete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void createAppointmentForMissingPatientReturns404() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/appointments", appointmentBody(999999L, "2030-01-01T10:00:00Z", "Checkup"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listPatientAppointments() throws Exception {
        long patientId = createPatient("John", "Doe");
        createAppointment(patientId, "2030-02-01T09:00:00Z");
        createAppointment(patientId, "2030-01-15T09:00:00Z");

        ResponseEntity<String> response = restTemplate.getForEntity("/patients/" + patientId + "/appointments", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode appointments = objectMapper.readTree(response.getBody());
        assertThat(appointments.size()).isEqualTo(2);
        assertThat(appointments.findValuesAsText("reason")).containsExactly("Annual checkup", "Annual checkup");
    }

    @Test
    void updateAppointmentStatus() throws Exception {
        long patientId = createPatient("John", "Doe");
        long id = createAppointment(patientId, "2030-01-01T10:00:00Z");

        ResponseEntity<String> patch = restTemplate.exchange(
                "/appointments/" + id + "/status", HttpMethod.PATCH,
                jsonEntity(statusBody("COMPLETED")), String.class);
        assertThat(patch.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(objectMapper.readTree(patch.getBody()).path("status").asText()).isEqualTo("COMPLETED");

        ResponseEntity<String> get = restTemplate.getForEntity("/appointments/" + id, String.class);
        assertThat(objectMapper.readTree(get.getBody()).path("status").asText()).isEqualTo("COMPLETED");
    }

    @Test
    void updateAppointmentStatusWithNullReturns400() throws Exception {
        long patientId = createPatient("John", "Doe");
        long id = createAppointment(patientId, "2030-01-01T10:00:00Z");

        ResponseEntity<String> patch = restTemplate.exchange(
                "/appointments/" + id + "/status", HttpMethod.PATCH,
                jsonEntity(statusBody(null)), String.class);
        assertThat(patch.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createAppointmentWithInvalidStatusReturns400() throws Exception {
        long patientId = createPatient("John", "Doe");
        Map<String, Object> body = appointmentBody(patientId, "2030-01-01T10:00:00Z", "Checkup");
        body.put("status", "NOT_A_REAL_STATUS");

        ResponseEntity<String> response = restTemplate.postForEntity("/appointments", body, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void listAppointmentsForMissingPatientReturns404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/patients/999999/appointments", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void medicationLifecycle() throws Exception {
        long patientId = createPatient("John", "Doe");

        ResponseEntity<String> create = restTemplate.postForEntity(
                "/medications", medicationBody(patientId, "Aspirin"), String.class);
        assertThat(create.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode created = objectMapper.readTree(create.getBody());
        long id = created.path("id").asLong();
        assertThat(created.path("patientName").asText()).isEqualTo("John Doe");
        assertThat(created.path("name").asText()).isEqualTo("Aspirin");
        assertThat(created.path("active").asBoolean()).isTrue();

        ResponseEntity<String> list = restTemplate.getForEntity("/medications", String.class);
        assertThat(objectMapper.readTree(list.getBody()).path("content").size()).isEqualTo(1);

        ResponseEntity<String> get = restTemplate.getForEntity("/medications/" + id, String.class);
        assertThat(objectMapper.readTree(get.getBody()).path("id").asLong()).isEqualTo(id);

        Map<String, Object> update = medicationBody(patientId, "Aspirin");
        update.put("dosage", "100mg");
        ResponseEntity<String> put = restTemplate.exchange(
                "/medications/" + id, HttpMethod.PUT, jsonEntity(update), String.class);
        assertThat(objectMapper.readTree(put.getBody()).path("dosage").asText()).isEqualTo("100mg");

        ResponseEntity<Void> delete = restTemplate.exchange(
                "/medications/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertThat(delete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void createMedicationForMissingPatientReturns404() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/medications", medicationBody(999999L, "Aspirin"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createMedicationWithEndDateBeforeStartDateReturns400() throws Exception {
        long patientId = createPatient("John", "Doe");
        Map<String, Object> body = medicationBody(patientId, "Aspirin");
        body.put("endDate", "2023-12-31");

        ResponseEntity<String> response = restTemplate.postForEntity("/medications", body, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void listPatientMedications() throws Exception {
        long patientId = createPatient("John", "Doe");
        createMedication(patientId, "Aspirin");
        createMedication(patientId, "Ibuprofen");

        ResponseEntity<String> response = restTemplate.getForEntity("/patients/" + patientId + "/medications", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode medications = objectMapper.readTree(response.getBody());
        assertThat(medications.size()).isEqualTo(2);
        assertThat(medications.findValuesAsText("name")).containsExactlyInAnyOrder("Aspirin", "Ibuprofen");
    }

    @Test
    void listMedicationsForMissingPatientReturns404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/patients/999999/medications", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void prometheusMetricsExposeBusinessCounters() throws Exception {
        createPatient("John", "Doe");

        // The Prometheus scrape endpoint produces text/plain (Prometheus format),
        // so send an explicit Accept header to avoid content-negotiation issues.
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
        ResponseEntity<String> response = restTemplate.exchange(
                "/actuator/prometheus", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("patients_created_total");
    }

    @Test
    void actuatorHealthEndpointIsAvailable() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("UP");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private long createPatient(String firstName, String lastName) throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/patients", patientBody(firstName, lastName), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return objectMapper.readTree(response.getBody()).path("id").asLong();
    }

    private long createAppointment(long patientId, String scheduledAt) throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/appointments", appointmentBody(patientId, scheduledAt, "Annual checkup"), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return objectMapper.readTree(response.getBody()).path("id").asLong();
    }

    private long createMedication(long patientId, String name) throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/medications", medicationBody(patientId, name), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return objectMapper.readTree(response.getBody()).path("id").asLong();
    }

    private Map<String, Object> patientBody(String firstName, String lastName) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("email", (firstName + "." + lastName + "@example.com").toLowerCase());
        body.put("dateOfBirth", "1990-05-20");
        return body;
    }

    private Map<String, Object> appointmentBody(long patientId, String scheduledAt, String reason) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("patientId", patientId);
        body.put("scheduledAt", scheduledAt);
        body.put("reason", reason);
        return body;
    }

    private Map<String, Object> medicationBody(long patientId, String name) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("patientId", patientId);
        body.put("name", name);
        body.put("dosage", "500mg");
        body.put("frequency", "Twice daily");
        body.put("startDate", "2024-01-01");
        return body;
    }

    private Map<String, Object> statusBody(String status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        return body;
    }

    private HttpEntity<Map<String, Object>> jsonEntity(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
