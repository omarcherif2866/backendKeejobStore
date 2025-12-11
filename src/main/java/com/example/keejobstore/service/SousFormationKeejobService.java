package com.example.keejobstore.service;

import com.example.keejobstore.entity.SousFormationkeejob;

import java.util.List;

public interface SousFormationKeejobService {
    SousFormationkeejob addSousFormation(SousFormationkeejob sousFormation);

    SousFormationkeejob updateSousFormation(Long id, SousFormationkeejob sousFormation);

    void deleteSousFormation(Long id);

    SousFormationkeejob getById(Long id);

    List<SousFormationkeejob> getAll();

    List<SousFormationkeejob> getSousFormationKeejobByFormationKeejob(Long formationId);

    void assignLogicielsToSousFormation(Long sousFormationId, List<Long> logicielsIds);

}
