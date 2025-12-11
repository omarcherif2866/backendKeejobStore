package com.example.keejobstore.service;

import com.example.keejobstore.entity.EvaluationCatalogue;
import com.example.keejobstore.entity.EvaluationCatalogue;

import java.util.List;

public interface EvaluationCatalogueService {

    EvaluationCatalogue addEvaluationCatalogue(EvaluationCatalogue EvaluationCatalogues);
    void deleteEvaluationCatalogueEntityById(Long id);
    EvaluationCatalogue getEvaluationCatalogueById(Long id);
    public List<EvaluationCatalogue> getAllEvaluationCatalogues();
    EvaluationCatalogue updateEvaluationCatalogue(Long id, EvaluationCatalogue EvaluationCatalogue);
    
    List<EvaluationCatalogue> getEvaluationCatalogueByEvaluation(Long evaluationId);

}
