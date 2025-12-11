package com.example.keejobstore.service;

import com.example.keejobstore.entity.Logiciel;
import com.example.keejobstore.entity.Partenaire;

import java.util.List;

public interface PartenaireService {

    Partenaire addPartenaire(Partenaire Partenaires);
    void deletePartenaireEntityById(Long id);
    Partenaire getPartenaireById(Long id);
    public List<Partenaire> getAllPartenaires();
    Partenaire updatePartenaire(Long id, Partenaire Partenaire);
    List<Partenaire> getPartenaireBySousFormationKeejob(Long sousFormationId);
    List<Partenaire> getPartenaireByFormationKeejob(Long FormationId);
    List<Partenaire> getPartenaireByEvaluation(Long EvaluationId);

}
