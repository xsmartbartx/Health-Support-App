package com.example.servicea.dto;

import com.example.servicea.model.Medication;
import com.example.servicea.model.Patient;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Outbound representation of a medication.
 */
public record MedicationResponse(
        Long id,
        Long patientId,
        String patientName,
        String name,
        String dosage,
        String frequency,
        LocalDate startDate,
        LocalDate endDate,
        boolean active,
        Instant createdAt) {

    public static MedicationResponse from(Medication medication) {
        Patient patient = medication.getPatient();
        return new MedicationResponse(
                medication.getId(),
                patient.getId(),
                patient.getFirstName() + " " + patient.getLastName(),
                medication.getName(),
                medication.getDosage(),
                medication.getFrequency(),
                medication.getStartDate(),
                medication.getEndDate(),
                medication.isActive(),
                medication.getCreatedAt());
    }
}
