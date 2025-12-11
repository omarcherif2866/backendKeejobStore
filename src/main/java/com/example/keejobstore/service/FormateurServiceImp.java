package com.example.keejobstore.service;

import com.example.keejobstore.entity.Formateur;
import com.example.keejobstore.entity.Formateur;
import com.example.keejobstore.entity.Formateur;
import com.example.keejobstore.repository.FormateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class FormateurServiceImp implements FormateurService {

    private final FormateurRepository formateurRepository;



    @Override
    public Formateur addFormateur(Formateur Formateurs) {
        try {
            return formateurRepository.save(Formateurs);
        } catch (DataIntegrityViolationException e) {
            // Gérer l'erreur de clé dupliquée ici
            throw new IllegalArgumentException("Erreur lors de l'ajout de l'Formateur : Cette Formateur existe déjà.");
        } catch (Exception e) {
            // Gérer les autres exceptions ici
            throw new RuntimeException("Une erreur s'est produite lors du traitement de la demande : " + e.getMessage());
        }
    }

    @Override
    public void deleteFormateurEntityById(Long id) {
        formateurRepository.deleteById(id);

    }

    @Override
    public Formateur getFormateurById(Long id) {
        return formateurRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("formateur not found"));
    }

    @Override
    public List<Formateur> getAllFormateurs() {
        List<Formateur> FormateursList = formateurRepository.findAll();
        Set<Formateur> FormateursSet = new HashSet<>(FormateursList);

        return new ArrayList<>(FormateursSet);  // ✔ maintenant c’est une List
    }

    @Override
    public Formateur updateFormateur(Long id, Formateur Formateur) {
        try {
            Formateur existingFormateur = formateurRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Formateur not found"));

            if (Formateur.getAddress() != null) {
                existingFormateur.setAddress(Formateur.getAddress());
            }
            if (Formateur.getDescription() != null) {
                existingFormateur.setDescription(Formateur.getDescription());
            }
            if (Formateur.getEmail() != null) {
                existingFormateur.setEmail(Formateur.getEmail());
            }
            if (Formateur.getPhone() != null) {
                existingFormateur.setPhone(Formateur.getPhone());
            }
            if (Formateur.getExperience() != null) {
                existingFormateur.setExperience(Formateur.getExperience());
            }
            if (Formateur.getPoste() != null) {
                existingFormateur.setPoste(Formateur.getPoste());
            }
            if (Formateur.getFirstName() != null) {
                existingFormateur.setFirstName(Formateur.getFirstName());
            }
            if (Formateur.getLastName() != null) {
                existingFormateur.setLastName(Formateur.getLastName());
            }
            if (Formateur.getUniversity() != null) {
                existingFormateur.setUniversity(Formateur.getUniversity());
            }


            Formateur updatedFormateur = formateurRepository.save(existingFormateur);

            return updatedFormateur;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Formateur not found with ID: " + id);
        }
    }
}
