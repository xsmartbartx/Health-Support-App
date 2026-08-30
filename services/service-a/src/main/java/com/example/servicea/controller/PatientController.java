package com.example.servicea.controller;

import com.example.servicea.dto.AppointmentResponse;
import com.example.servicea.dto.MedicationResponse;
import com.example.servicea.dto.PatientRequest;
import com.example.servicea.dto.PatientResponse;
import com.example.servicea.service.AppointmentService;
import com.example.servicea.service.MedicationService;
import com.example.servicea.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patients", description = "Patient management API")
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final MedicationService medicationService;

    public PatientController(PatientService patientService,
                             AppointmentService appointmentService,
                             MedicationService medicationService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.medicationService = medicationService;
    }

    @GetMapping
    @Operation(summary = "List patients (paginated)")
    public Page<PatientResponse> list(
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return patientService.list(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a patient by id")
    public PatientResponse get(@PathVariable Long id) {
        return patientService.get(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search patients by first name, last name or email")
    public List<PatientResponse> search(@RequestParam("q") String query) {
        return patientService.search(query);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new patient")
    public PatientResponse create(@Valid @RequestBody PatientRequest request) {
        return patientService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing patient")
    public PatientResponse update(@PathVariable Long id, @Valid @RequestBody PatientRequest request) {
        return patientService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a patient")
    public void delete(@PathVariable Long id) {
        patientService.delete(id);
    }

    @GetMapping("/{id}/appointments")
    @Operation(summary = "List a patient's appointments")
    public List<AppointmentResponse> appointments(@PathVariable Long id) {
        return appointmentService.listByPatient(id);
    }

    @GetMapping("/{id}/medications")
    @Operation(summary = "List a patient's medications")
    public List<MedicationResponse> medications(@PathVariable Long id) {
        return medicationService.listByPatient(id);
    }
}
