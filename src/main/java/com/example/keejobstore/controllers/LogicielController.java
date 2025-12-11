package com.example.keejobstore.controllers;

import com.example.keejobstore.entity.Logiciel;
import com.example.keejobstore.entity.SousFormationkeejob;
import com.example.keejobstore.repository.SousFormationKeejobRepository;
import com.example.keejobstore.service.CloudinaryService;
import com.example.keejobstore.service.LogicielService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/logiciel")
@RequiredArgsConstructor
public class LogicielController {

    private final LogicielService logicielService;
    private final CloudinaryService cloudinaryService;
    private final SousFormationKeejobRepository sousFormationKeejobRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addLogiciel(
            @RequestParam("name") String name,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "sousFormationKeejobId", required = true) Long sousFormationKeejobId) {
        try {
            // Validation
            if (name == null || name.isEmpty()) {
                return ResponseEntity.badRequest().body("Paramètres d'entrée invalides.");
            }

            Logiciel logiciel = new Logiciel();
            logiciel.setName(name);

            // Upload de l'image vers Cloudinary
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                logiciel.setImage(imageUrl);
            }

            // Récupérer la sous-formation depuis la base
            SousFormationkeejob sf = sousFormationKeejobRepository.findById(sousFormationKeejobId)
                    .orElseThrow(() -> new RuntimeException("SousFormation non trouvée"));

            // Ajouter la sous-formation à la liste du logiciel
            logiciel.getSousFormationLogiciel().add(sf);

            // Sauvegarder le logiciel
            Logiciel savedLogiciel = logicielService.addLogiciel(logiciel);

            // Optionnel : ajouter le logiciel à la sous-formation (côté propriétaire)
            sf.getSousFormationLogiciel().add(savedLogiciel);
            sousFormationKeejobRepository.save(sf);

            return ResponseEntity.ok(savedLogiciel);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload de l'image : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur s'est produite lors du traitement de la demande : " + e.getMessage());
        }
    }



    @GetMapping("{id}")
    public Logiciel getLogicielById(@PathVariable Long id){
        return logicielService.getLogicielById(id);
    }

    @DeleteMapping("{id}")
    public void deleteLogiciel(@PathVariable Long id) {
        logicielService.deleteLogicielEntityById(id);
    }

    @GetMapping("/allLogiciels")
    public ResponseEntity<List<Logiciel>> getAllLogiciels() {
        List<Logiciel> Logiciels = logicielService.getAllLogiciels();
        return ResponseEntity.ok(Logiciels);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateLogiciel(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            Logiciel existing = logicielService.getLogicielById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actualité non trouvée");
            }

            // MAJ des champs
            existing.setName(name);

            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                existing.setImage(imageUrl);
            }

            Logiciel saved = logicielService.updateLogiciel(id, existing);
            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload de l'image : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }


   @GetMapping("/sousFormation/{sousFormationId}")
    public ResponseEntity<List<Logiciel>> getBySousFormation(@PathVariable Long sousFormationId) {
        List<Logiciel> logiciel = logicielService.getLogicielBySousFormationKeejob(sousFormationId);
        return ResponseEntity.ok(logiciel);
    }


}
