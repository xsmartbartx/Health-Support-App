package com.example.servicea.service;

import com.example.servicea.dto.PatientRequest;
import com.example.servicea.dto.PatientResponse;
import com.example.servicea.exception.ResourceNotFoundException;
import com.example.servicea.model.Patient;
import com.example.servicea.repository.PatientRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository repository;
    private final MeterRegistry meterRegistry;

    public PatientService(PatientRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
    }

    public Page<PatientResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(PatientResponse::from);
    }

    public PatientResponse get(Long id) {
        return PatientResponse.from(findById(id));
    }

    public List<PatientResponse> search(String query) {
        return repository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query, query, query)
                .stream()
                .map(PatientResponse::from)
                .toList();
    }

    @Transactional
    public PatientResponse create(PatientRequest request) {
        Patient patient = new Patient(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getDateOfBirth());
        PatientResponse response = PatientResponse.from(repository.save(patient));
        meterRegistry.counter("patients.created").increment();
        return response;
    }

    @Transactional
    public PatientResponse update(Long id, PatientRequest request) {
        Patient patient = findById(id);
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setEmail(request.getEmail());
        patient.setDateOfBirth(request.getDateOfBirth());
        return PatientResponse.from(repository.save(patient));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
        meterRegistry.counter("patients.deleted").increment();
    }

    private Patient findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }
}
