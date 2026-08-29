package com.example.servicea.service;

import com.example.servicea.dto.MedicationRequest;
import com.example.servicea.dto.MedicationResponse;
import com.example.servicea.exception.ResourceNotFoundException;
import com.example.servicea.model.Medication;
import com.example.servicea.model.Patient;
import com.example.servicea.repository.MedicationRepository;
import com.example.servicea.repository.PatientRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MedicationService {

    private final MedicationRepository repository;
    private final PatientRepository patientRepository;
    private final MeterRegistry meterRegistry;

    public MedicationService(MedicationRepository repository,
                             PatientRepository patientRepository,
                             MeterRegistry meterRegistry) {
        this.repository = repository;
        this.patientRepository = patientRepository;
        this.meterRegistry = meterRegistry;
    }

    public Page<MedicationResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(MedicationResponse::from);
    }

    public MedicationResponse get(Long id) {
        return MedicationResponse.from(findById(id));
    }

    public List<MedicationResponse> listByPatient(Long patientId) {
        return repository.findByPatientIdOrderByStartDateDesc(patientId).stream()
                .map(MedicationResponse::from)
                .toList();
    }

    @Transactional
    public MedicationResponse create(MedicationRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        Medication medication = new Medication();
        medication.setPatient(patient);
        medication.setName(request.getName());
        medication.setDosage(request.getDosage());
        medication.setFrequency(request.getFrequency());
        medication.setStartDate(request.getStartDate());
        medication.setEndDate(request.getEndDate());
        if (request.getActive() != null) {
            medication.setActive(request.getActive());
        }

        MedicationResponse response = MedicationResponse.from(repository.save(medication));
        meterRegistry.counter("medications.created").increment();
        return response;
    }

    @Transactional
    public MedicationResponse update(Long id, MedicationRequest request) {
        Medication medication = findById(id);
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        medication.setPatient(patient);
        medication.setName(request.getName());
        medication.setDosage(request.getDosage());
        medication.setFrequency(request.getFrequency());
        medication.setStartDate(request.getStartDate());
        medication.setEndDate(request.getEndDate());
        if (request.getActive() != null) {
            medication.setActive(request.getActive());
        }
        return MedicationResponse.from(repository.save(medication));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
        meterRegistry.counter("medications.deleted").increment();
    }

    private Medication findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + id));
    }
}
