package com.example.keejobstore.service;

import com.example.keejobstore.entity.Formateur;
import com.example.keejobstore.entity.ServiceFromateur;
import com.example.keejobstore.entity.TitleWhy;
import com.example.keejobstore.repository.FormateurRepository;
import com.example.keejobstore.repository.TitleWhyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TitleWhyServiceImp implements TitleWhyService {

    private final TitleWhyRepository titleWhyRepository;
    private final FormateurRepository formateurRepository;

    @Override
    public List<TitleWhy> getTitleWhyByFormateur(Long formateurId) {
        Optional<Formateur> formateurOpt = formateurRepository.findById(formateurId);
        if (formateurOpt.isPresent()) {
            return titleWhyRepository.findByFormateurId(formateurId);
        } else {
            throw new RuntimeException("Formateur not found with id: " + formateurId);
        }
    }

    @Override
    public TitleWhy add(TitleWhy titleWhy) {
        Formateur formateur = validateAndGetFormateur(titleWhy);
        titleWhy.setFormateur(formateur);
        return titleWhyRepository.save(titleWhy);
    }

    // Méthode privée pour valider le formateur
    private Formateur validateAndGetFormateur(TitleWhy titleWhy) {
        if (titleWhy.getFormateur() == null || titleWhy.getFormateur().getId() == null) {
            throw new IllegalArgumentException("Formateur non fourni pour le service.");
        }

        return formateurRepository.findById(titleWhy.getFormateur().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Formateur non trouvé avec l'id: " + titleWhy.getFormateur().getId()
                ));
    }

    @Override
    public TitleWhy update(Long id, TitleWhy titleWhy) {
        TitleWhy existing = titleWhyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ServiceFormateur not found with id: " + id));

        existing.setTitle(titleWhy.getTitle()); // ou autres champs que tu veux mettre à jour
        existing.setDescription(titleWhy.getDescription());

        if (titleWhy.getFormateur() != null && titleWhy.getFormateur().getId() != null) {
            Formateur formateur = formateurRepository.findById(titleWhy.getFormateur().getId())
                    .orElseThrow(() -> new RuntimeException("Formateur not found with id: " + titleWhy.getFormateur().getId()));
            existing.setFormateur(formateur);
        }

        return titleWhyRepository.save(existing);
    }

    @Override
    public TitleWhy getById(Long id) {
        return titleWhyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id: " + id));
    }
}
