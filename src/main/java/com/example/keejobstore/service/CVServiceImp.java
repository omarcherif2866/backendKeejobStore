package com.example.keejobstore.service;

import com.example.keejobstore.entity.CVandLetter;
import com.example.keejobstore.repository.CVRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class CVServiceImp implements CVService {

    private final CVRepository cvRepository ;

    @Override
    public CVandLetter addCVandLetter(CVandLetter CVandLetters) {
        try {
            return cvRepository.save(CVandLetters);
        } catch (DataIntegrityViolationException e) {
            // Gérer l'erreur de clé dupliquée ici
            throw new IllegalArgumentException("Erreur lors de l'ajout de l'CVandLetter : Cette CVandLetters existe déjà.");
        } catch (Exception e) {
            // Gérer les autres exceptions ici
            throw new RuntimeException("Une erreur s'est produite lors du traitement de la demande : " + e.getMessage());
        }
    }

    @Override
    public void deleteCVandLetterEntityById(Long id) {
        cvRepository.deleteById(id);

    }

    @Override
    public CVandLetter getCVandLetterById(Long id) {
        return cvRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CVandLetter not found"));
    }

    @Override
    public List<CVandLetter> getAllCVandLetters() {
        List<CVandLetter> CVandLettersList = cvRepository.findAll();
        Set<CVandLetter> CVandLettersSet = new HashSet<>(CVandLettersList);

        return new ArrayList<>(CVandLettersSet);  // ✔ maintenant c’est une List
    }

    @Override
    public CVandLetter updateCVandLetter(Long id, CVandLetter newData) {

        CVandLetter existingCVandLetter = cvRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CVandLetter not found"));

        // 🔹 name
        if (newData.getName() != null) {
            existingCVandLetter.setName(newData.getName());
        }

        // 🔹 description
        if (newData.getDescription() != null) {
            existingCVandLetter.setDescription(newData.getDescription());
        }

        // 🔹 image
        if (newData.getImage() != null) {
            existingCVandLetter.setImage(newData.getImage());
        }

        // 🔹 MISE À JOUR DES SECTIONS
        if (newData.getSections() != null && !newData.getSections().isEmpty()) {
            existingCVandLetter.setSections(newData.getSections());
        }

        if (newData.getPriceSections() != null && !newData.getPriceSections().isEmpty()) {
            existingCVandLetter.setPriceSections(newData.getPriceSections());
        }

        return cvRepository.save(existingCVandLetter);
    }

}
