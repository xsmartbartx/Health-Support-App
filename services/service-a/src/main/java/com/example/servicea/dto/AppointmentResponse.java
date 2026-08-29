package com.example.servicea.dto;

import com.example.servicea.model.Appointment;
import com.example.servicea.model.AppointmentStatus;
import com.example.servicea.model.Patient;

import java.time.Instant;

/**
 * Outbound representation of an appointment.
 */
public record AppointmentResponse(
        Long id,
        Long patientId,
        String patientName,
        Instant scheduledAt,
        String reason,
        AppointmentStatus status,
        String notes,
        Instant createdAt) {

    public static AppointmentResponse from(Appointment appointment) {
        Patient patient = appointment.getPatient();
        return new AppointmentResponse(
                appointment.getId(),
                patient.getId(),
                patient.getFirstName() + " " + patient.getLastName(),
                appointment.getScheduledAt(),
                appointment.getReason(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCreatedAt());
    }
}
