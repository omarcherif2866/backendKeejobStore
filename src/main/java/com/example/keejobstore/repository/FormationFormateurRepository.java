package com.example.keejobstore.repository;

import com.example.keejobstore.entity.FormationFormateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormationFormateurRepository extends JpaRepository<FormationFormateur,Long > {
    List<FormationFormateur> findByFormateurId(Long formateurId);

}
