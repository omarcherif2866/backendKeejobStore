package com.example.keejobstore.service;

import com.example.keejobstore.entity.DetailObject;
import com.example.keejobstore.entity.Evaluation;
import com.example.keejobstore.entity.FormationKeejob;
import com.example.keejobstore.entity.FormationKeejob;
import com.example.keejobstore.repository.ActualiteRepository;
import com.example.keejobstore.repository.FormationKeejobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FormationKeejobServiceImp implements FormationKeejobService {

    private final FormationKeejobRepository formationKeejobRepository;

    @Override
    public FormationKeejob addFormationKeejob(FormationKeejob FormationKeejob) {
        try {
            return formationKeejobRepository.save(FormationKeejob);
        } catch (DataIntegrityViolationException e) {
            // Gérer l'erreur de clé dupliquée ici
            throw new IllegalArgumentException("Erreur lors de l'ajout de l'activité : Cette activité existe déjà.");
        } catch (Exception e) {
            // Gérer les autres exceptions ici
            throw new RuntimeException("Une erreur s'est produite lors du traitement de la demande : " + e.getMessage());
        }
    }

    @Override
    public void deleteFormationKeejobEntityById(Long id) {
        formationKeejobRepository.deleteById(id);

    }

    @Override
    public FormationKeejob getFormationKeejobById(Long id) {
        return formationKeejobRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FormationKeejob not found"));
    }

    @Override
    public List<FormationKeejob> getAllFormationKeejobs() {
        List<FormationKeejob> FormationKeejobList = formationKeejobRepository.findAll();
        Set<FormationKeejob> FormationKeejobSet = new HashSet<>(FormationKeejobList);

        return new ArrayList<>(FormationKeejobSet);  // ✔ maintenant c’est une List
    }

    @Override
    public FormationKeejob updateFormationKeejob(Long id, FormationKeejob FormationKeejob) {
        try {
            FormationKeejob existingFormationKeejob = formationKeejobRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("FormationKeejob not found"));

            if (FormationKeejob.getTitle() != null) {
                existingFormationKeejob.setTitle(FormationKeejob.getTitle());
            }
            if (FormationKeejob.getDescription() != null) {
                existingFormationKeejob.setDescription(FormationKeejob.getDescription());
            }

            if (FormationKeejob.getImage() != null) {
                existingFormationKeejob.setImage(FormationKeejob.getImage());
            }

            if (FormationKeejob.getLogo() != null) {
                existingFormationKeejob.setLogo(FormationKeejob.getLogo());
            }


            if (FormationKeejob.getPartenaires() != null) {
                // Supprimer les anciennes relations
                existingFormationKeejob.getPartenaires().clear();
                // Ajouter les nouvelles
                existingFormationKeejob.setPartenaires(FormationKeejob.getPartenaires());
            }

            FormationKeejob updatedFormationKeejob = formationKeejobRepository.save(existingFormationKeejob);

            return updatedFormationKeejob;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("FormationKeejob not found with ID: " + id);
        }
    }


}
