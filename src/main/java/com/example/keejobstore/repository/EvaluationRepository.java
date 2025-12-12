package com.example.keejobstore.repository;

import com.example.keejobstore.entity.CategoryEvaluation;
import com.example.keejobstore.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation,Long > {
    List<Evaluation> findByEvaluationCategory(CategoryEvaluation category);

}
