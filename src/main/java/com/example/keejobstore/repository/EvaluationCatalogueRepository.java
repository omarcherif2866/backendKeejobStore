package com.example.keejobstore.repository;

import com.example.keejobstore.entity.EvaluationCatalogue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationCatalogueRepository extends JpaRepository<EvaluationCatalogue,Long > {
    List<EvaluationCatalogue> findByEvaluationId(Long evaluationId);

}
