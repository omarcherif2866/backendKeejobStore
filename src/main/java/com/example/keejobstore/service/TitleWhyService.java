package com.example.keejobstore.service;

import com.example.keejobstore.entity.ServiceFromateur;
import com.example.keejobstore.entity.TitleWhy;

import java.util.List;

public interface TitleWhyService {
    List<TitleWhy> getTitleWhyByFormateur(Long formateurId);

    TitleWhy add(TitleWhy titleWhy);      // Ajouter un service

    TitleWhy update(Long id, TitleWhy titleWhy); // Mettre à jour un service existant

    TitleWhy getById(Long id); // ✅ Ajoutez cette ligne

}
