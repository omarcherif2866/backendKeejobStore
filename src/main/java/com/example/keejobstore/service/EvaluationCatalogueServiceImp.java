package com.example.keejobstore.service;

import com.example.keejobstore.entity.EvaluationCatalogue;
import com.example.keejobstore.entity.Evaluation;
import com.example.keejobstore.entity.EvaluationCatalogue;
import com.example.keejobstore.repository.EvaluationCatalogueRepository;
import com.example.keejobstore.repository.EvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;


@RequiredArgsConstructor
@Service
public class EvaluationCatalogueServiceImp implements EvaluationCatalogueService {
    private final EvaluationCatalogueRepository evaluationCatalogueRepository;
    private final EvaluationRepository evaluationRepository;

    @Override
    public EvaluationCatalogue addEvaluationCatalogue(EvaluationCatalogue EvaluationCatalogues) {
        try {
            return evaluationCatalogueRepository.save(EvaluationCatalogues);
        } catch (DataIntegrityViolationException e) {
            // Gérer l'erreur de clé dupliquée ici
            throw new IllegalArgumentException("Erreur lors de l'ajout de l'EvaluationCatalogues : Cette EvaluationCatalogues existe déjà.");
        } catch (Exception e) {
            // Gérer les autres exceptions ici
            throw new RuntimeException("Une erreur s'est produite lors du traitement de la demande : " + e.getMessage());
        }
    }

    @Override
    public void deleteEvaluationCatalogueEntityById(Long id) {
        evaluationCatalogueRepository.deleteById(id);

    }

    @Override
    public EvaluationCatalogue getEvaluationCatalogueById(Long id) {
        return evaluationCatalogueRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("EvaluationCatalogue not found"));
    }

    @Override
    public List<EvaluationCatalogue> getAllEvaluationCatalogues() {
        List<EvaluationCatalogue> EvaluationCataloguesList = evaluationCatalogueRepository.findAll();
        Set<EvaluationCatalogue> EvaluationCataloguesSet = new HashSet<>(EvaluationCataloguesList);

        return new ArrayList<>(EvaluationCataloguesSet);  // ✔ maintenant c’est une List
    }

    @Override
    public EvaluationCatalogue updateEvaluationCatalogue(Long id, EvaluationCatalogue EvaluationCatalogue) {
        try {
            EvaluationCatalogue existingEvaluationCatalogue = evaluationCatalogueRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("EvaluationCatalogue not found"));

            if (EvaluationCatalogue.getTitle() != null) {
                existingEvaluationCatalogue.setTitle(EvaluationCatalogue.getTitle());
            }



            EvaluationCatalogue updatedEvaluationCatalogue = evaluationCatalogueRepository.save(existingEvaluationCatalogue);

            return updatedEvaluationCatalogue;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("EvaluationCatalogue not found with ID: " + id);
        }
    }

    @Override
    public List<EvaluationCatalogue> getEvaluationCatalogueByEvaluation(Long evaluationId) {
        Optional<Evaluation> evaluationOpt = evaluationRepository.findById(evaluationId);
        if (evaluationOpt.isPresent()) {
            return evaluationCatalogueRepository.findByEvaluationId(evaluationId);
        } else {
            throw new RuntimeException("Formateur not found with id: " + evaluationId);
        }
    }
}

