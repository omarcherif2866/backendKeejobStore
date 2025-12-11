package com.example.keejobstore.service;

import com.example.keejobstore.entity.Formateur;
import com.example.keejobstore.entity.FormationFormateur;
import com.example.keejobstore.repository.FormateurRepository;
import com.example.keejobstore.repository.FormationFormateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FormationFormateurServiceImp implements FormationFormateurService {
    private final FormationFormateurRepository formationRepo;
    private final FormateurRepository formateurRepo;

    @Override
    public List<FormationFormateur> getAll() {
        return formationRepo.findAll();
    }

    @Override
    public FormationFormateur add(FormationFormateur formationFormateur) {
        // S'assurer que la relation est bien établie
        if (formationFormateur.getFormateur() != null) {
            Formateur formateur = formationFormateur.getFormateur();
            formationFormateur.setFormateur(formateur);
        }
        return formationRepo.save(formationFormateur);
    }

    @Override
    public List<FormationFormateur> getByFormateur(Long formateurId) {
        return formationRepo.findByFormateurId(formateurId);
    }

    @Override
    public FormationFormateur getById(Long id) {
        return formationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id: " + id));
    }

}
