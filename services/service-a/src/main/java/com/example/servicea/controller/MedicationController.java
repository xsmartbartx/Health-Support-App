package com.example.servicea.controller;

import com.example.servicea.dto.MedicationRequest;
import com.example.servicea.dto.MedicationResponse;
import com.example.servicea.service.MedicationService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medications")
@Tag(name = "Medications", description = "Medication management API")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping
    @Operation(summary = "List medications (paginated)")
    public Page<MedicationResponse> list(
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return medicationService.list(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a medication by id")
    public MedicationResponse get(@PathVariable Long id) {
        return medicationService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new medication")
    public MedicationResponse create(@Valid @RequestBody MedicationRequest request) {
        return medicationService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing medication")
    public MedicationResponse update(@PathVariable Long id, @Valid @RequestBody MedicationRequest request) {
        return medicationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a medication")
    public void delete(@PathVariable Long id) {
        medicationService.delete(id);
    }
}
