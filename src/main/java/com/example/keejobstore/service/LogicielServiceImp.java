package com.example.keejobstore.service;

import com.example.keejobstore.entity.Logiciel;
import com.example.keejobstore.entity.SousFormationkeejob;
import com.example.keejobstore.repository.LogicielRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class LogicielServiceImp implements LogicielService {

    private final LogicielRepository logicielRepository;

    @Override
    public Logiciel addLogiciel(Logiciel Logiciels) {
        try {
            return logicielRepository.save(Logiciels);
        } catch (DataIntegrityViolationException e) {
            // Gérer l'erreur de clé dupliquée ici
            throw new IllegalArgumentException("Erreur lors de l'ajout de l'activité : Cette activité existe déjà.");
        } catch (Exception e) {
            // Gérer les autres exceptions ici
            throw new RuntimeException("Une erreur s'est produite lors du traitement de la demande : " + e.getMessage());
        }
    }


    @Override
    public void deleteLogicielEntityById(Long id) {
        logicielRepository.deleteById(id);

    }

    @Override
    public Logiciel getLogicielById(Long id) {
        return logicielRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Logiciels not found"));
    }

    @Override
    public List<Logiciel> getAllLogiciels() {
        List<Logiciel> LogicielsList = logicielRepository.findAll();
        Set<Logiciel> LogicielsSet = new HashSet<>(LogicielsList);

        return new ArrayList<>(LogicielsSet);  // ✔ maintenant c’est une List
    }


    @Override
    public Logiciel updateLogiciel(Long id, Logiciel Logiciel) {
        try {
            Logiciel existingLogiciels = logicielRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Logiciels not found"));

            if (Logiciel.getName() != null) {
                existingLogiciels.setName(Logiciel.getName());
            }
            if (Logiciel.getImage() != null) {
                existingLogiciels.setImage(Logiciel.getImage());
            }



            Logiciel updatedLogiciels = logicielRepository.save(existingLogiciels);

            return updatedLogiciels;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Logiciels not found with ID: " + id);
        }
    }

    @Override
    public List<Logiciel> getLogicielBySousFormationKeejob(Long sousFormationId) {
        return logicielRepository.findBySousFormationLogicielId(sousFormationId);
    }
}
