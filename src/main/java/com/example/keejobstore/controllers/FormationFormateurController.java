package com.example.keejobstore.controllers;

import com.example.keejobstore.entity.Formateur;
import com.example.keejobstore.entity.FormationFormateur;
import com.example.keejobstore.repository.FormateurRepository;
import com.example.keejobstore.service.FormationFormateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/formationsFormateur")
@RequiredArgsConstructor
public class FormationFormateurController {
    private final FormationFormateurService formationFormateurService;
    private final FormateurRepository formateurRepo;

    @GetMapping("/all")
    public ResponseEntity<List<FormationFormateur>> getAll() {
        return ResponseEntity.ok(formationFormateurService.getAll());
    }

    // ADD
    @PostMapping()
    public ResponseEntity<?> addFormation(@RequestBody FormationFormateur formation) {

        // Debug
        System.out.println("Formation reçue: " + formation);
        System.out.println("Formateur ID reçu: " +
                (formation.getFormateur() != null ? formation.getFormateur().getId() : "null"));

        if (formation.getFormateur() != null && formation.getFormateur().getId() != null) {
            Optional<Formateur> f = formateurRepo.findById(formation.getFormateur().getId());
            if (f.isEmpty()) {
                return ResponseEntity.badRequest().body("Formateur introuvable.");
            }
            formation.setFormateur(f.get());

            // Debug après assignation
            System.out.println("Formateur assigné: " + formation.getFormateur().getId());
        } else {
            return ResponseEntity.badRequest().body("Formateur non fourni.");
        }

        FormationFormateur saved = formationFormateurService.add(formation);

        // Debug après sauvegarde
        System.out.println("Formation sauvegardée - Formateur ID: " +
                (saved.getFormateur() != null ? saved.getFormateur().getId() : "null"));

        return ResponseEntity.ok(saved);
    }

    // GET BY FORMATEUR ID
    @GetMapping("/byFormateur/{id}")
    public ResponseEntity<List<FormationFormateur>> getByFormateur(@PathVariable Long id) {
        return ResponseEntity.ok(formationFormateurService.getByFormateur(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFormationById(@PathVariable Long id) {
        try {
            FormationFormateur formation = formationFormateurService.getById(id);
            return ResponseEntity.ok(formation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFormation(@PathVariable Long id, @RequestBody FormationFormateur formation) {

        // Debug
        System.out.println("Formation reçue pour update: " + formation);
        System.out.println("Formateur ID reçu: " +
                (formation.getFormateur() != null ? formation.getFormateur().getId() : "null"));

        // Vérifier si la formation existe
        try {
            FormationFormateur existingFormation = formationFormateurService.getById(id);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

        // Vérifier si formateur existe
        if (formation.getFormateur() != null && formation.getFormateur().getId() != null) {
            Optional<Formateur> f = formateurRepo.findById(formation.getFormateur().getId());
            if (f.isEmpty()) {
                return ResponseEntity.badRequest().body("Formateur introuvable.");
            }
            formation.setFormateur(f.get());
        } else {
            return ResponseEntity.badRequest().body("Formateur non fourni.");
        }

        formation.setId(id); // Important : s'assurer que l'ID est bien défini
        FormationFormateur updated = formationFormateurService.add(formation); // save() fait aussi l'update

        System.out.println("Formation mise à jour - Formateur ID: " +
                (updated.getFormateur() != null ? updated.getFormateur().getId() : "null"));

        return ResponseEntity.ok(updated);
    }
}

