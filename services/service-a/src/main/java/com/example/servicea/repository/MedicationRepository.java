package com.example.servicea.repository;

import com.example.servicea.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

    List<Medication> findByPatientIdOrderByStartDateDesc(Long patientId);
}
