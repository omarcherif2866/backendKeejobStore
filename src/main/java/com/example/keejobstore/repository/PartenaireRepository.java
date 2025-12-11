package com.example.keejobstore.repository;

import com.example.keejobstore.entity.Logiciel;
import com.example.keejobstore.entity.Partenaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartenaireRepository extends JpaRepository<Partenaire,Long > {
    List<Partenaire> findBySousFormationsId(Long sousFormationId);
    List<Partenaire> findByFormationsId(Long sousFormationId);
    List<Partenaire> findByEvaluationsId(Long evaluationId);

}
