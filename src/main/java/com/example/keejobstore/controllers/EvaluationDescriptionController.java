package com.example.keejobstore.controllers;

import com.example.keejobstore.entity.EvaluationCatalogue;
import com.example.keejobstore.service.CloudinaryService;
import com.example.keejobstore.service.EvaluationCatalogueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/evaluationDescription")
@RequiredArgsConstructor
public class EvaluationDescriptionController {
    private final EvaluationCatalogueService evaluationCategoryService;
    private final CloudinaryService cloudinaryService;

    @GetMapping("/byEvaluation/{evaluationId}")
    public List<EvaluationCatalogue> getEvaluationDescriptionByEvaluation(@PathVariable Long evaluationId) {
        return evaluationCategoryService.getEvaluationCatalogueByEvaluation(evaluationId);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addEvaluationCatalogue(
            @RequestParam("title") String title,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            // Validation
            if (title == null || title.isEmpty()) {
                return ResponseEntity.badRequest().body("Paramètres d'entrée invalides.");
            }

            EvaluationCatalogue EvaluationCatalogue = new EvaluationCatalogue();
            EvaluationCatalogue.setTitle(title);


            // Upload de l'image vers Cloudinary
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                EvaluationCatalogue.setImage(imageUrl);
            }

            EvaluationCatalogue savedEvaluationCatalogue = evaluationCategoryService.addEvaluationCatalogue(EvaluationCatalogue);

            return ResponseEntity.ok(savedEvaluationCatalogue);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload de l'image : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur s'est produite lors du traitement de la demande : " + e.getMessage());
        }
    }


    @GetMapping("{id}")
    public EvaluationCatalogue getEvaluationCatalogueById(@PathVariable Long id){
        return evaluationCategoryService.getEvaluationCatalogueById(id);
    }

    @DeleteMapping("{id}")
    public void deleteEvaluationCatalogue(@PathVariable Long id) {
        evaluationCategoryService.deleteEvaluationCatalogueEntityById(id);
    }

    @GetMapping("/allEvaluationCatalogues")
    public ResponseEntity<List<EvaluationCatalogue>> getAllEvaluationCatalogues() {
        List<EvaluationCatalogue> EvaluationCatalogues = evaluationCategoryService.getAllEvaluationCatalogues();
        return ResponseEntity.ok(EvaluationCatalogues);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateEvaluationCatalogue(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            EvaluationCatalogue existing = evaluationCategoryService.getEvaluationCatalogueById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actualité non trouvée");
            }

            // MAJ des champs
            existing.setTitle(title);

            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                existing.setImage(imageUrl);
            }

            EvaluationCatalogue saved = evaluationCategoryService.updateEvaluationCatalogue(id, existing);
            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload de l'image : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }




}
    
