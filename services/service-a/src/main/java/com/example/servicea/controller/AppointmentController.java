package com.example.servicea.controller;

import com.example.servicea.dto.AppointmentRequest;
import com.example.servicea.dto.AppointmentResponse;
import com.example.servicea.dto.AppointmentStatusRequest;
import com.example.servicea.service.AppointmentService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointments")
@Tag(name = "Appointments", description = "Appointment management API")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    @Operation(summary = "List appointments (paginated)")
    public Page<AppointmentResponse> list(
            @ParameterObject @PageableDefault(size = 20, sort = "scheduledAt") Pageable pageable) {
        return appointmentService.list(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an appointment by id")
    public AppointmentResponse get(@PathVariable Long id) {
        return appointmentService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new appointment")
    public AppointmentResponse create(@Valid @RequestBody AppointmentRequest request) {
        return appointmentService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing appointment")
    public AppointmentResponse update(@PathVariable Long id, @Valid @RequestBody AppointmentRequest request) {
        return appointmentService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update an appointment's status")
    public AppointmentResponse updateStatus(@PathVariable Long id,
            @Valid @RequestBody AppointmentStatusRequest request) {
        return appointmentService.updateStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an appointment")
    public void delete(@PathVariable Long id) {
        appointmentService.delete(id);
    }
}
