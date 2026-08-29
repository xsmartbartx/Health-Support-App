package com.example.servicea.service;

import com.example.servicea.dto.AppointmentRequest;
import com.example.servicea.dto.AppointmentResponse;
import com.example.servicea.exception.ResourceNotFoundException;
import com.example.servicea.model.Appointment;
import com.example.servicea.model.Patient;
import com.example.servicea.repository.AppointmentRepository;
import com.example.servicea.repository.PatientRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository repository;
    private final PatientRepository patientRepository;
    private final MeterRegistry meterRegistry;

    public AppointmentService(AppointmentRepository repository,
                              PatientRepository patientRepository,
                              MeterRegistry meterRegistry) {
        this.repository = repository;
        this.patientRepository = patientRepository;
        this.meterRegistry = meterRegistry;
    }

    public Page<AppointmentResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(AppointmentResponse::from);
    }

    public AppointmentResponse get(Long id) {
        return AppointmentResponse.from(findById(id));
    }

    public List<AppointmentResponse> listByPatient(Long patientId) {
        return repository.findByPatientIdOrderByScheduledAtAsc(patientId).stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setScheduledAt(request.getScheduledAt());
        appointment.setReason(request.getReason());
        if (request.getStatus() != null) {
            appointment.setStatus(request.getStatus());
        }
        appointment.setNotes(request.getNotes());

        AppointmentResponse response = AppointmentResponse.from(repository.save(appointment));
        meterRegistry.counter("appointments.created").increment();
        return response;
    }

    @Transactional
    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = findById(id);
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        appointment.setPatient(patient);
        appointment.setScheduledAt(request.getScheduledAt());
        appointment.setReason(request.getReason());
        if (request.getStatus() != null) {
            appointment.setStatus(request.getStatus());
        }
        appointment.setNotes(request.getNotes());
        return AppointmentResponse.from(repository.save(appointment));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
        meterRegistry.counter("appointments.deleted").increment();
    }

    private Appointment findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }
}
