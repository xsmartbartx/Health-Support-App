package com.example.servicea.repository;

import com.example.servicea.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByScheduledAtAsc(Long patientId);
}
