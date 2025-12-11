package com.example.keejobstore.service;


import com.example.keejobstore.entity.Formateur;

import java.util.List;

public interface FormateurService {
    Formateur addFormateur(Formateur Formateurs);
    void deleteFormateurEntityById(Long id);
    Formateur getFormateurById(Long id);
    public List<Formateur> getAllFormateurs();
    Formateur updateFormateur(Long id, Formateur Formateur);



}
