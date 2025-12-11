package com.example.keejobstore.service;

import com.example.keejobstore.entity.Formateur;
import com.example.keejobstore.entity.FormationFormateur;
import com.example.keejobstore.entity.ServiceFromateur;
import com.example.keejobstore.repository.FormateurRepository;
import com.example.keejobstore.repository.ServiceFormateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ServiceFormateurServiceImp implements ServiceFormateurService {

    private final ServiceFormateurRepository serviceFormateurRepository;
    private final FormateurRepository formateurRepository;

    @Override
    public List<ServiceFromateur> getServiceFormateurByFormateur(Long formateurId) {
        Optional<Formateur> formateurOpt = formateurRepository.findById(formateurId);
        if (formateurOpt.isPresent()) {
            return serviceFormateurRepository.findByFormateurId(formateurId);
        } else {
            throw new RuntimeException("Formateur not found with id: " + formateurId);
        }
    }


    @Override
    public ServiceFromateur add(ServiceFromateur service) {
        if (service.getFormateur() != null && service.getFormateur().getId() != null) {
            Formateur formateur = formateurRepository.findById(service.getFormateur().getId())
                    .orElseThrow(() -> new RuntimeException("Formateur not found with id: " + service.getFormateur().getId()));
            service.setFormateur(formateur);
        } else {
            throw new RuntimeException("Formateur non fourni pour le service.");
        }
        return serviceFormateurRepository.save(service);
    }

    @Override
    public ServiceFromateur update(Long id, ServiceFromateur service) {
        ServiceFromateur existing = serviceFormateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ServiceFormateur not found with id: " + id));

        existing.setTitle(service.getTitle()); // ou autres champs que tu veux mettre à jour
        existing.setDescription(service.getDescription());

        if (service.getFormateur() != null && service.getFormateur().getId() != null) {
            Formateur formateur = formateurRepository.findById(service.getFormateur().getId())
                    .orElseThrow(() -> new RuntimeException("Formateur not found with id: " + service.getFormateur().getId()));
            existing.setFormateur(formateur);
        }

        return serviceFormateurRepository.save(existing);
    }

    @Override
    public ServiceFromateur getById(Long id) {
        return serviceFormateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id: " + id));
    }
}
