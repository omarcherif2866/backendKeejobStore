package com.example.keejobstore.service;

import com.example.keejobstore.entity.Logiciel;
import com.example.keejobstore.entity.SousFormationkeejob;
import com.example.keejobstore.repository.LogicielRepository;
import com.example.keejobstore.repository.SousFormationKeejobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class SousFormationKeejobServiceImp implements SousFormationKeejobService {
    private final SousFormationKeejobRepository sousFormationKeejobRepository;
    private final LogicielRepository logicielRepository;


    @Override
    public SousFormationkeejob addSousFormation(SousFormationkeejob SousFormationkeejob) {
        try {
            return sousFormationKeejobRepository.save(SousFormationkeejob);
        } catch (DataIntegrityViolationException e) {
            // Gérer l'erreur de clé dupliquée ici
            throw new IllegalArgumentException("Erreur lors de l'ajout de l'activité : Cette activité existe déjà.");
        } catch (Exception e) {
            // Gérer les autres exceptions ici
            throw new RuntimeException("Une erreur s'est produite lors du traitement de la demande : " + e.getMessage());
        }
    }

    @Override
    public void deleteSousFormation(Long id) {
        sousFormationKeejobRepository.deleteById(id);

    }

    @Override
    public SousFormationkeejob getById(Long id) {
        return sousFormationKeejobRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SousFormationkeejob not found"));
    }

    @Override
    public List<SousFormationkeejob> getAll() {
        List<SousFormationkeejob> SousFormationkeejobList = sousFormationKeejobRepository.findAll();
        Set<SousFormationkeejob> SousFormationkeejobSet = new HashSet<>(SousFormationkeejobList);

        return new ArrayList<>(SousFormationkeejobSet);  // ✔ maintenant c’est une List
    }

    @Override
    public SousFormationkeejob updateSousFormation(Long id, SousFormationkeejob SousFormationkeejob) {
        try {
            SousFormationkeejob existingSousFormationkeejob = sousFormationKeejobRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("SousFormationkeejob not found"));

            if (SousFormationkeejob.getTitle() != null) {
                existingSousFormationkeejob.setTitle(SousFormationkeejob.getTitle());
            }
            if (SousFormationkeejob.getDescription() != null) {
                existingSousFormationkeejob.setDescription(SousFormationkeejob.getDescription());
            }

            if (SousFormationkeejob.getLogo() != null) {
                existingSousFormationkeejob.setLogo(SousFormationkeejob.getLogo());
            }

            SousFormationkeejob updatedSousFormationkeejob = sousFormationKeejobRepository.save(existingSousFormationkeejob);

            return updatedSousFormationkeejob;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("SousFormationkeejob not found with ID: " + id);
        }
    }

    @Override
    public List<SousFormationkeejob> getSousFormationKeejobByFormationKeejob(Long formationId) {
        return sousFormationKeejobRepository.findByFormationId(formationId);
    }

    @Override
    @Transactional
    public void assignLogicielsToSousFormation(Long sousFormationId, List<Long> logicielsIds) {

        SousFormationkeejob sf = sousFormationKeejobRepository.findById(sousFormationId)
                .orElseThrow(() -> new RuntimeException("Sous formation introuvable"));

        List<Logiciel> logiciels = logicielRepository.findAllById(logicielsIds);

        sf.setSousFormationLogiciel(logiciels); // ⚡ met tous les logiciels sélectionnés

        sousFormationKeejobRepository.save(sf);
    }

}


