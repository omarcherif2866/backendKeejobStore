package com.example.keejobstore.repository;

import com.example.keejobstore.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation,Long > {
}
