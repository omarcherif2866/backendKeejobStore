package com.example.keejobstore.controllers;

import com.example.keejobstore.entity.*;
import com.example.keejobstore.repository.PartenaireRepository;
import com.example.keejobstore.service.CloudinaryService;
import com.example.keejobstore.service.EvaluationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/evaluation")
@RequiredArgsConstructor
public class EvaluationController {
    private final EvaluationService evaluationService;
    private final CloudinaryService cloudinaryService;
    private final PartenaireRepository partenaireRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addEvaluation(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("sections") String sectionsJson,
            @RequestParam("evaluationCategory") String evaluationCategoryStr,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds,
            @RequestParam(value = "catalogueTitles", required = false) List<String> catalogueTitles,
            @RequestParam(value = "catalogueImages", required = false) List<MultipartFile> catalogueImages,
            @RequestParam(value = "iconFiles", required = false) List<MultipartFile> iconFiles) { // <- NOUVEAU

        try {
            // Validation
            if (name == null || name.isEmpty() || description == null || description.isEmpty()) {
                return ResponseEntity.badRequest().body("Paramètres d'entrée invalides.");
            }

            // Convert JSON → List<EvaluationSection>
            ObjectMapper mapper = new ObjectMapper();
            List<EvaluationSection> sections =
                    mapper.readValue(sectionsJson, new TypeReference<List<EvaluationSection>>() {});

            // Convert String → Enum
            CategoryEvaluation category;
            try {
                category = CategoryEvaluation.valueOf(evaluationCategoryStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Catégorie d'évaluation invalide !");
            }

            // Upload des icônes et mise à jour des sections
            if (iconFiles != null && !iconFiles.isEmpty()) {
                int iconIndex = 0;
                for (EvaluationSection section : sections) {
                    if (section.getDetails() != null) {
                        for (DetailObject detail : section.getDetails()) {
                            // Si l'icône existe et n'est pas vide
                            if (iconIndex < iconFiles.size()
                                    && iconFiles.get(iconIndex) != null
                                    && !iconFiles.get(iconIndex).isEmpty()) {

                                // Upload vers Cloudinary dans le dossier "icon"
                                String iconUrl = cloudinaryService.uploadIcon(
                                        iconFiles.get(iconIndex),
                                        "icon"
                                );
                                detail.setIcon(iconUrl);
                            }
                            iconIndex++;
                        }
                    }
                }
            }

            Evaluation evaluation = new Evaluation();
            evaluation.setName(name);
            evaluation.setDescription(description);
            evaluation.setSections(sections);
            evaluation.setEvaluationCategory(category);

            // Upload image principale
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                evaluation.setImage(imageUrl);
            }

            // Upload logo
            if (logo != null && !logo.isEmpty()) {
                String logoUrl = cloudinaryService.uploadImage(logo);
                evaluation.setLogo(logoUrl); // <- CORRIGÉ: c'était setImage avant
            }

            // Gestion des partenaires
            if (partenairesIds != null && !partenairesIds.isEmpty()) {
                List<Partenaire> partenaires = partenaireRepository.findAllById(partenairesIds);
                evaluation.setEvaluationPartenaires(partenaires);
            }

            // Gestion des catalogues
            if (catalogueTitles != null && !catalogueTitles.isEmpty()) {
                List<EvaluationCatalogue> catalogues = new ArrayList<>();
                for (int i = 0; i < catalogueTitles.size(); i++) {
                    EvaluationCatalogue catalogue = new EvaluationCatalogue();
                    catalogue.setTitle(catalogueTitles.get(i));

                    if (catalogueImages != null && i < catalogueImages.size()
                            && catalogueImages.get(i) != null && !catalogueImages.get(i).isEmpty()) {
                        String catalogueImageUrl = cloudinaryService.uploadImage(catalogueImages.get(i));
                        catalogue.setImage(catalogueImageUrl);
                    }

                    catalogue.setEvaluation(evaluation);
                    catalogues.add(catalogue);
                }
                evaluation.setEvaluationCatalogues(catalogues);
            }

            Evaluation saved = evaluationService.addEvaluation(evaluation);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur : " + e.getMessage());
        }
    }




    @GetMapping("{id}")
    public Evaluation getEvaluationById(@PathVariable Long id) {
        return evaluationService.getEvaluationById(id);
    }

    @DeleteMapping("{id}")
    public void deleteEvaluation(@PathVariable Long id) {
        evaluationService.deleteEvaluationEntityById(id);
    }

    @GetMapping("/allEvaluations")
    public ResponseEntity<List<Evaluation>> getAllEvaluations() {
        List<Evaluation> Evaluations = evaluationService.getAllEvaluations();
        return ResponseEntity.ok(Evaluations);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateEvaluation(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "sections", required = false) String sectionsJson,
            @RequestParam(value = "evaluationCategory", required = false) String evaluationCategoryStr, // <- ajouté
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds,
            @RequestParam(value = "catalogueTitles", required = false) List<String> catalogueTitles,
            @RequestParam(value = "catalogueImages", required = false) List<MultipartFile> catalogueImages) {

        try {
            Evaluation existing = evaluationService.getEvaluationById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Évaluation non trouvée");
            }

            if (name != null) existing.setName(name);
            if (description != null) existing.setDescription(description);

            // Mise à jour de la catégorie
            if (evaluationCategoryStr != null && !evaluationCategoryStr.isEmpty()) {
                try {
                    CategoryEvaluation category = CategoryEvaluation.valueOf(evaluationCategoryStr);
                    existing.setEvaluationCategory(category);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Catégorie d'évaluation invalide !");
                }
            }

            // Mise à jour des sections
            if (sectionsJson != null && !sectionsJson.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                List<EvaluationSection> sections =
                        mapper.readValue(sectionsJson, new TypeReference<List<EvaluationSection>>() {});
                existing.setSections(sections);
            }

            // Mise à jour image principale
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                existing.setImage(imageUrl);
            }

            if (logo != null && !logo.isEmpty()) {
                String logoUrl = cloudinaryService.uploadImage(logo);
                existing.setLogo(logoUrl);
            }

            // Mise à jour partenaires
            if (partenairesIds != null) {
                List<Partenaire> partenaires = partenaireRepository.findAllById(partenairesIds);
                existing.setEvaluationPartenaires(partenaires);
            }

            // Mise à jour catalogues
            if (catalogueTitles != null) {
                existing.getEvaluationCatalogues().clear();

                for (int i = 0; i < catalogueTitles.size(); i++) {
                    EvaluationCatalogue catalogue = new EvaluationCatalogue();
                    catalogue.setTitle(catalogueTitles.get(i));

                    if (catalogueImages != null && i < catalogueImages.size()
                            && catalogueImages.get(i) != null && !catalogueImages.get(i).isEmpty()) {
                        String catalogueImageUrl = cloudinaryService.uploadImage(catalogueImages.get(i));
                        catalogue.setImage(catalogueImageUrl);
                    }

                    catalogue.setEvaluation(existing);
                    existing.getEvaluationCatalogues().add(catalogue);
                }
            }

            Evaluation saved = evaluationService.updateEvaluation(id, existing);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }



    @GetMapping("/categories")
    public List<String> getAllCategories() {
        return evaluationService.getAllCategories();
    }

    // Optionnel : récupérer les détails groupés par catégorie
    @GetMapping("/by-category")
    public Map<String, List<DetailObject>> getDetailsByCategory() {
        return evaluationService.getDetailsByCategory();
    }

}
    
