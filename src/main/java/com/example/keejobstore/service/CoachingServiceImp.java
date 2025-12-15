package com.example.keejobstore.service;

import com.example.keejobstore.entity.*;
import com.example.keejobstore.entity.CoachingEmploi;
import com.example.keejobstore.repository.CoachingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CoachingServiceImp implements CoachingService {

    private final CoachingRepository coachingRepository;

    @Override
    public CoachingEmploi addCoachingEmploi(CoachingEmploi CoachingEmplois) {
        try {
            return coachingRepository.save(CoachingEmplois);
        } catch (DataIntegrityViolationException e) {
            // Gérer l'erreur de clé dupliquée ici
            throw new IllegalArgumentException("Erreur lors de l'ajout de l'CoachingEmploi : Cette CoachingEmploi existe déjà.");
        } catch (Exception e) {
            // Gérer les autres exceptions ici
            throw new RuntimeException("Une erreur s'est produite lors du traitement de la demande : " + e.getMessage());
        }
    }


    @Override
    public void deleteCoachingEmploiEntityById(Long id) {
        coachingRepository.deleteById(id);

    }

    @Override
    public CoachingEmploi getCoachingEmploiById(Long id) {
        return coachingRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CoachingEmploi not found"));
    }

    @Override
    public List<CoachingEmploi> getAllCoachingEmplois() {
        List<CoachingEmploi> CoachingEmploiList = coachingRepository.findAll();
        Set<CoachingEmploi> CoachingEmploiSet = new HashSet<>(CoachingEmploiList);

        return new ArrayList<>(CoachingEmploiSet);  // ✔ maintenant c’est une List
    }

    @Override
    public CoachingEmploi updateCoachingEmploi(Long id, CoachingEmploi newData) {

        CoachingEmploi existingCoachingEmploi = coachingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CoachingEmploi not found"));

        // 🔹 name
        if (newData.getName() != null) {
            existingCoachingEmploi.setName(newData.getName());
        }

        if (newData.getTitre() != null) {
            existingCoachingEmploi.setTitre(newData.getTitre());
        }

        if (newData.getSousTitre() != null) {
            existingCoachingEmploi.setSousTitre(newData.getSousTitre());
        }

        // 🔹 description
        if (newData.getDescription() != null) {
            existingCoachingEmploi.setDescription(newData.getDescription());
        }

        // 🔹 image
        if (newData.getImage() != null) {
            existingCoachingEmploi.setImage(newData.getImage());
        }

        if (newData.getLogo() != null) {
            existingCoachingEmploi.setLogo(newData.getLogo());
        }

        // 🔹 MISE À JOUR DES SECTIONS
        if (newData.getSections() != null && !newData.getSections().isEmpty()) {
            existingCoachingEmploi.setSections(newData.getSections());
        }

        if (newData.getPriceSections() != null && !newData.getPriceSections().isEmpty()) {
            existingCoachingEmploi.setPriceSections(newData.getPriceSections());
        }

        if (newData.getCategoryCoaching() != null) {
            existingCoachingEmploi.setCategoryCoaching(newData.getCategoryCoaching());
        }

        return coachingRepository.save(existingCoachingEmploi);
    }


    public List<CoachingEmploi> findByCategoryCoaching (CategoryCoaching category) {
        return coachingRepository.findByCategoryCoaching(category);
    }

}
