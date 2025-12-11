package com.example.keejobstore.service;

import com.example.keejobstore.entity.ServiceFromateur;

import java.util.List;

public interface ServiceFormateurService {

    List<ServiceFromateur> getServiceFormateurByFormateur(Long formateurId);

    ServiceFromateur add(ServiceFromateur service);      // Ajouter un service

    ServiceFromateur update(Long id, ServiceFromateur service); // Mettre à jour un service existant

    ServiceFromateur getById(Long id); // ✅ Ajoutez cette ligne

}

