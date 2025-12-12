package com.example.keejobstore.service;

import com.example.keejobstore.entity.CategoryEvaluation;
import com.example.keejobstore.entity.DetailObject;
import com.example.keejobstore.entity.Evaluation;

import java.util.List;
import java.util.Map;

public interface EvaluationService {
    Evaluation addEvaluation(Evaluation Evaluations);
    void deleteEvaluationEntityById(Long id);
    Evaluation getEvaluationById(Long id);
    public List<Evaluation> getAllEvaluations();
    Evaluation updateEvaluation(Long id, Evaluation Evaluation);
    List<String> getAllCategories();
    Map<String, List<DetailObject>> getDetailsByCategory();
    List<Evaluation> getEvaluationsByCategory(CategoryEvaluation category);

}
