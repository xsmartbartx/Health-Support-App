package com.example.servicea.dto;

import com.example.servicea.model.Patient;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Outbound representation of a patient (never exposes the JPA entity directly).
 */
public record PatientResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDate dateOfBirth,
        Instant createdAt) {

    public static PatientResponse from(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getDateOfBirth(),
                patient.getCreatedAt());
    }
}
