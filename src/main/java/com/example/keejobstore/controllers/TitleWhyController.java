package com.example.keejobstore.controllers;

import com.example.keejobstore.entity.TitleWhy;
import com.example.keejobstore.entity.TitleWhy;
import com.example.keejobstore.service.TitleWhyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/titleWhy")
@RequiredArgsConstructor
public class TitleWhyController {
    private final TitleWhyService titleWhyService;

    @GetMapping("/byFormateur/{formateurId}")
    public List<TitleWhy> getTitleWhyByFormateur(@PathVariable Long formateurId) {
        return titleWhyService.getTitleWhyByFormateur(formateurId);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody TitleWhy titleWhy) {
        try {
            TitleWhy saved = titleWhyService.add(titleWhy);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            // Si formateur non fourni
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            // Si formateur non trouvé
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Autres erreurs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TitleWhy> update(@PathVariable Long id, @RequestBody TitleWhy titleWhy) {
        return ResponseEntity.ok(titleWhyService.update(id, titleWhy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable Long id) {
        try {
            TitleWhy titleWhy = titleWhyService.getById(id);
            return ResponseEntity.ok(titleWhy);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
