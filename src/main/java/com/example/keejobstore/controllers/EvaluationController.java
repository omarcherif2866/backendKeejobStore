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
            @RequestParam(value = "iconFiles", required = false) List<MultipartFile> iconFiles) {

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

            // ✅ CORRECTION COMPLÈTE: Matching correct des icônes avec les détails
            if (iconFiles != null && !iconFiles.isEmpty()) {
                int iconIndex = 0; // Index global pour parcourir iconFiles

                // Parcourir toutes les sections
                for (EvaluationSection section : sections) {
                    if (section.getDetails() != null && !section.getDetails().isEmpty()) {

                        // Parcourir tous les détails de cette section
                        for (DetailObject detail : section.getDetails()) {

                            // Vérifier qu'on n'a pas dépassé la liste des iconFiles
                            if (iconIndex < iconFiles.size()) {
                                MultipartFile iconFile = iconFiles.get(iconIndex);

                                // Vérifier si c'est un vrai fichier (pas un placeholder vide)
                                if (iconFile != null && !iconFile.isEmpty() && iconFile.getSize() > 0) {
                                    try {
                                        // Upload vers Cloudinary dans le dossier "icon"
                                        String iconUrl = cloudinaryService.uploadIcon(iconFile, "icon");
                                        detail.setIcon(iconUrl);
                                        System.out.println("✅ Icon uploaded for detail '" + detail.getTitre() + "': " + iconUrl);
                                    } catch (Exception e) {
                                        System.err.println("❌ Error uploading icon: " + e.getMessage());
                                        detail.setIcon(null);
                                    }
                                } else {
                                    // Placeholder vide ou fichier vide
                                    // Garder l'URL existante si elle existe, sinon mettre null
                                    String existingIcon = detail.getIcon();
                                    if (existingIcon == null || existingIcon.trim().isEmpty()) {
                                        detail.setIcon(null);
                                        System.out.println("ℹ️ No icon for detail '" + detail.getTitre() + "' (set to null)");
                                    } else {
                                        // Garder l'URL existante (cas de l'édition)
                                        System.out.println("ℹ️ Keeping existing icon for detail '" + detail.getTitre() + "': " + existingIcon);
                                    }
                                }

                                iconIndex++; // Passer au fichier suivant
                            } else {
                                // Plus d'iconFiles disponibles, mettre null
                                detail.setIcon(null);
                                System.out.println("⚠️ No more iconFiles, setting null for detail '" + detail.getTitre() + "'");
                            }
                        }
                    }
                }

                System.out.println("📊 Total iconFiles received: " + iconFiles.size());
                System.out.println("📊 Total details processed: " + iconIndex);
            } else {
                // Pas d'iconFiles fournis, s'assurer que tous les icons sont null
                for (EvaluationSection section : sections) {
                    if (section.getDetails() != null) {
                        for (DetailObject detail : section.getDetails()) {
                            if (detail.getIcon() == null || detail.getIcon().trim().isEmpty()) {
                                detail.setIcon(null);
                            }
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
                evaluation.setLogo(logoUrl);
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
            e.printStackTrace();
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
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("sections") String sectionsJson,
            @RequestParam("evaluationCategory") String evaluationCategoryStr,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds,
            @RequestParam(value = "catalogueTitles", required = false) List<String> catalogueTitles,
            @RequestParam(value = "catalogueImages", required = false) List<MultipartFile> catalogueImages,
            @RequestParam(value = "iconFiles", required = false) List<MultipartFile> iconFiles) {

        try {
            Evaluation evaluation = evaluationService.getEvaluationById(id);
            if (evaluation == null) {
                return ResponseEntity.notFound().build();
            }

            evaluation.setName(name);
            evaluation.setDescription(description);

            CategoryEvaluation category;
            try {
                category = CategoryEvaluation.valueOf(evaluationCategoryStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Catégorie d'évaluation invalide !");
            }
            evaluation.setEvaluationCategory(category);

            ObjectMapper mapper = new ObjectMapper();
            List<EvaluationSection> sections =
                    mapper.readValue(sectionsJson, new TypeReference<List<EvaluationSection>>() {});

            // ✅ MÊME LOGIQUE DE MATCHING pour l'édition
            if (iconFiles != null && !iconFiles.isEmpty()) {
                int iconIndex = 0;

                for (EvaluationSection section : sections) {
                    if (section.getDetails() != null && !section.getDetails().isEmpty()) {
                        for (DetailObject detail : section.getDetails()) {
                            if (iconIndex < iconFiles.size()) {
                                MultipartFile iconFile = iconFiles.get(iconIndex);

                                if (iconFile != null && !iconFile.isEmpty() && iconFile.getSize() > 0) {
                                    try {
                                        String iconUrl = cloudinaryService.uploadIcon(iconFile, "icon");
                                        detail.setIcon(iconUrl);
                                        System.out.println("✅ Icon updated for detail '" + detail.getTitre() + "': " + iconUrl);
                                    } catch (Exception e) {
                                        System.err.println("❌ Error uploading icon: " + e.getMessage());
                                        // Garder l'icône existante en cas d'erreur
                                    }
                                } else {
                                    String existingIcon = detail.getIcon();
                                    if (existingIcon == null || existingIcon.trim().isEmpty()) {
                                        detail.setIcon(null);
                                        System.out.println("ℹ️ No icon for detail '" + detail.getTitre() + "' (set to null)");
                                    } else {
                                        System.out.println("ℹ️ Keeping existing icon for detail '" + detail.getTitre() + "': " + existingIcon);
                                    }
                                }

                                iconIndex++;
                            }
                        }
                    }
                }

                System.out.println("📊 UPDATE - Total iconFiles received: " + iconFiles.size());
                System.out.println("📊 UPDATE - Total details processed: " + iconIndex);
            } else {
                for (EvaluationSection section : sections) {
                    if (section.getDetails() != null) {
                        for (DetailObject detail : section.getDetails()) {
                            if (detail.getIcon() == null || detail.getIcon().trim().isEmpty()) {
                                detail.setIcon(null);
                            }
                        }
                    }
                }
            }

            evaluation.setSections(sections);

            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                evaluation.setImage(imageUrl);
            }

            if (logo != null && !logo.isEmpty()) {
                String logoUrl = cloudinaryService.uploadImage(logo);
                evaluation.setLogo(logoUrl);
            }

            if (partenairesIds != null) {
                List<Partenaire> partenaires = partenaireRepository.findAllById(partenairesIds);
                evaluation.setEvaluationPartenaires(partenaires);
            }

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

            Evaluation saved = evaluationService.updateEvaluation(id, evaluation);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur : " + e.getMessage());
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

    @GetMapping("/by-category/{category}")
    public ResponseEntity<?> getEvaluationByCategory(@PathVariable String category) {
        try {
            CategoryEvaluation enumValue = CategoryEvaluation.valueOf(category);
            List<Evaluation> evaluations = evaluationService.getEvaluationsByCategory(enumValue);
            return ResponseEntity.ok(evaluations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Catégorie invalide !");
        }
    }


}
    
