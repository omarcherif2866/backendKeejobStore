package com.example.keejobstore.service;

import com.example.keejobstore.entity.FormationKeejob;

import java.util.List;

public interface FormationKeejobService {
    FormationKeejob addFormationKeejob(FormationKeejob FormationKeejobs);
    void deleteFormationKeejobEntityById(Long id);
    FormationKeejob getFormationKeejobById(Long id);
    public List<FormationKeejob> getAllFormationKeejobs();
    FormationKeejob updateFormationKeejob(Long id, FormationKeejob FormationKeejob);
}
