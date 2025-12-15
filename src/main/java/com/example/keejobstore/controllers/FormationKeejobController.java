package com.example.keejobstore.controllers;

import com.example.keejobstore.entity.*;
import com.example.keejobstore.repository.PartenaireRepository;
import com.example.keejobstore.service.CloudinaryService;
import com.example.keejobstore.service.FormationKeejobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/formationKeejob")
@RequiredArgsConstructor
public class FormationKeejobController {

    private final FormationKeejobService formationKeejobService;
    private final CloudinaryService cloudinaryService;
    private final PartenaireRepository partenaireRepository;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addFormationKeejob(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam("categoryFormationKeejob") String categoryFormationKeejobStr,
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds) {

        try {
            // Validation
            if (title == null || title.isEmpty() || description == null || description.isEmpty()) {
                return ResponseEntity.badRequest().body("Paramètres d'entrée invalides.");
            }

            CategoryFormationKeejob category;
            try {
                category = CategoryFormationKeejob.valueOf(categoryFormationKeejobStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Catégorie d'évaluation invalide !");
            }

            // Création
            FormationKeejob formation = new FormationKeejob();
            formation.setTitle(title);
            formation.setDescription(description);
            formation.setCategoryFormationKeejob(category);

            // Upload de l'image vers Cloudinary
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                formation.setImage(imageUrl);
            }

            // Upload logo
            if (logo != null && !logo.isEmpty()) {
                String logoUrl = cloudinaryService.uploadImage(logo);
                formation.setLogo(logoUrl);
            }

            // 🔥 Association des partenaires (ManyToMany)
            if (partenairesIds != null && !partenairesIds.isEmpty()) {
                List<Partenaire> partenaires = partenaireRepository.findAllById(partenairesIds);
                formation.setPartenaires(partenaires);
            }

            // Enregistrer
            FormationKeejob saved = formationKeejobService.addFormationKeejob(formation);

            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload de l'image : " + e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur s'est produite lors du traitement : " + e.getMessage());
        }
    }



    @GetMapping("{id}")
    public FormationKeejob getFormationKeejobById(@PathVariable Long id){
        return formationKeejobService.getFormationKeejobById(id);
    }

    @DeleteMapping("{id}")
    public void deleteFormationKeejob(@PathVariable Long id) {
        formationKeejobService.deleteFormationKeejobEntityById(id);
    }

    @GetMapping("/allFormationKeejobs")
    public ResponseEntity<List<FormationKeejob>> getAllFormationKeejobs() {
        List<FormationKeejob> FormationKeejobs = formationKeejobService.getAllFormationKeejobs();
        return ResponseEntity.ok(FormationKeejobs);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateFormationKeejob(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam("categoryFormationKeejob") String categoryFormationKeejobStr,
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds) {

        try {
            FormationKeejob existing = formationKeejobService.getFormationKeejobById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FormationKeejob non trouvée");
            }
            CategoryFormationKeejob category;
            try {
                category = CategoryFormationKeejob.valueOf(categoryFormationKeejobStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Catégorie d'évaluation invalide !");
            }
            existing.setCategoryFormationKeejob(category);
            // MAJ des champs principaux
            existing.setTitle(title);
            existing.setDescription(description);

            // MAJ de l'image si fournie
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                existing.setImage(imageUrl);
            }

            if (logo != null && !logo.isEmpty()) {
                String logoUrl = cloudinaryService.uploadImage(logo);
                existing.setLogo(logoUrl);
            }

            // 🔥 MAJ des partenaires
            if (partenairesIds != null) {
                List<Partenaire> partenaires = partenaireRepository.findAllById(partenairesIds);
                existing.setPartenaires(partenaires);
            }

            FormationKeejob saved = formationKeejobService.updateFormationKeejob(id, existing);

            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload de l'image : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }


    @GetMapping("/by-category/{category}")
    public ResponseEntity<?> getFormationsByCategory(@PathVariable String category) {
        try {
            CategoryFormationKeejob enumValue = CategoryFormationKeejob.valueOf(category);
            List<FormationKeejob> cvs = formationKeejobService.findByCategoryFormationKeejob(enumValue);
            return ResponseEntity.ok(cvs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Catégorie invalide !");
        }
    }


}

