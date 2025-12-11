package com.example.keejobstore.controllers;
import java.util.Map;   // <-- IMPORTANT !!!

import com.fasterxml.jackson.core.type.TypeReference; // ✅ IMPORTANT : Bon import
import com.example.keejobstore.entity.DetailObject;
import com.example.keejobstore.entity.FormationKeejob;
import com.example.keejobstore.entity.Partenaire;
import com.example.keejobstore.entity.SousFormationkeejob;
import com.example.keejobstore.repository.FormationKeejobRepository;
import com.example.keejobstore.repository.PartenaireRepository;
import com.example.keejobstore.service.CloudinaryService;
import com.example.keejobstore.service.SousFormationKeejobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/sousFormationKeejob")
@RequiredArgsConstructor
public class SousFormationKeejobController {

    private final SousFormationKeejobService sousFormationKeejobService;
    private final FormationKeejobRepository formationKeejobRepository;
    private final CloudinaryService cloudinaryService;
    private final PartenaireRepository partenaireRepository;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addSousFormationKeejob(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("formationKeejobId") Long formationKeejobId,
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds,
            @RequestParam("details") String detailsJson,
            @RequestParam("titleLogiciel") String titleLogiciel,
            @RequestParam(value = "icons", required = false) MultipartFile[] icons // ⬅️ images des détails
    ) {

        try {
            FormationKeejob formation = formationKeejobRepository.findById(formationKeejobId)
                    .orElseThrow(() -> new RuntimeException("FormationKeejob introuvable"));

            SousFormationkeejob sousFormation = new SousFormationkeejob();
            sousFormation.setTitle(title);
            sousFormation.setDescription(description);
            sousFormation.setFormation(formation);
            sousFormation.setTitleLogiciel(titleLogiciel);

            // Upload image principale
            if (image != null && !image.isEmpty()) {
                sousFormation.setImage(cloudinaryService.uploadImage(image));
            }

            // Parse JSON des détails
            ObjectMapper mapper = new ObjectMapper();
            List<DetailObject> details = mapper.readValue(detailsJson, new TypeReference<List<DetailObject>>() {});

            // Vérifier que icons correspond au nombre de détails
            if (icons != null && icons.length > 0) {
                for (int i = 0; i < details.size(); i++) {
                    if (i < icons.length && icons[i] != null && !icons[i].isEmpty()) {
                        String iconUrl = cloudinaryService.uploadImage(icons[i]);
                        details.get(i).setIcon(iconUrl);
                    }
                }
            }

            sousFormation.setDetails(details);

            // Associer partenaires
            if (partenairesIds != null && !partenairesIds.isEmpty()) {
                sousFormation.setSousFormationPartenaires(
                        partenaireRepository.findAllById(partenairesIds)
                );
            }

            SousFormationkeejob saved = sousFormationKeejobService.addSousFormation(sousFormation);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur : " + e.getMessage());
        }
    }



    @GetMapping("{id}")
    public SousFormationkeejob getFormationKeejobById(@PathVariable Long id){
        return sousFormationKeejobService.getById(id);
    }

    @DeleteMapping("{id}")
    public void deleteFormationKeejob(@PathVariable Long id) {
        sousFormationKeejobService.deleteSousFormation(id);
    }

    @GetMapping("/allSousFormationKeejobs")
    public ResponseEntity<List<SousFormationkeejob>> getAllFormationKeejobs() {
        List<SousFormationkeejob> sousFormationkeejob = sousFormationKeejobService.getAll();
        return ResponseEntity.ok(sousFormationkeejob);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateSousFormationKeejob(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "titleLogiciel", required = false) String titleLogiciel, // ✅ AJOUT
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds,
            @RequestParam(value = "details", required = false) String detailsJson, // ✅ AJOUT
            @RequestParam(value = "icons", required = false) MultipartFile[] icons) { // ✅ AJOUT

        try {
            System.out.println("📥 PUT /sousFormationKeejob/" + id);
            System.out.println("📝 titleLogiciel reçu: " + titleLogiciel);
            System.out.println("📝 details reçu: " + detailsJson);
            System.out.println("📝 icons count: " + (icons != null ? icons.length : 0));

            SousFormationkeejob existing = sousFormationKeejobService.getById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("SousFormationkeejob non trouvée");
            }

            // ✅ MAJ des champs principaux
            existing.setTitle(title);
            existing.setDescription(description);

            // ✅ MAJ du titleLogiciel
            if (titleLogiciel != null) {
                existing.setTitleLogiciel(titleLogiciel);
                System.out.println("✅ titleLogiciel mis à jour: " + titleLogiciel);
            }

            // ✅ MAJ de l'image si fournie
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                existing.setImage(imageUrl);
                System.out.println("✅ Image mise à jour");
            }

            // ✅ MAJ des partenaires
            if (partenairesIds != null && !partenairesIds.isEmpty()) {
                List<Partenaire> partenaires = partenaireRepository.findAllById(partenairesIds);
                existing.setSousFormationPartenaires(partenaires);
                System.out.println("✅ Partenaires mis à jour: " + partenaires.size());
            }

            // ✅ MAJ des details (MÊME LOGIQUE QUE LE POST)
            if (detailsJson != null && !detailsJson.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                List<DetailObject> details = mapper.readValue(detailsJson, new TypeReference<List<DetailObject>>() {});

                System.out.println("📋 Parsing de " + details.size() + " détails");

                // Upload des nouvelles icônes si fournies
                if (icons != null && icons.length > 0) {
                    for (int i = 0; i < details.size(); i++) {
                        if (i < icons.length && icons[i] != null && !icons[i].isEmpty()) {
                            String iconUrl = cloudinaryService.uploadImage(icons[i]);
                            details.get(i).setIcon(iconUrl);
                            System.out.println("✅ Icône uploadée pour détail " + i);
                        } else if (existing.getDetails() != null && i < existing.getDetails().size()) {
                            // ✅ Garder l'ancienne icône si pas de nouvelle fournie
                            details.get(i).setIcon(existing.getDetails().get(i).getIcon());
                            System.out.println("♻️ Icône conservée pour détail " + i);
                        }
                    }
                } else if (existing.getDetails() != null) {
                    // ✅ Pas de nouvelles icônes fournies, conserver toutes les anciennes
                    for (int i = 0; i < details.size() && i < existing.getDetails().size(); i++) {
                        details.get(i).setIcon(existing.getDetails().get(i).getIcon());
                    }
                    System.out.println("♻️ Toutes les icônes conservées");
                }

                existing.setDetails(details);
                System.out.println("✅ Details mis à jour: " + details.size());
            }

            // ✅ Sauvegarder les modifications
            SousFormationkeejob saved = sousFormationKeejobService.updateSousFormation(id, existing);

            System.out.println("✅ SousFormation mise à jour avec succès!");

            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            System.err.println("❌ Erreur upload: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload de l'image : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur serveur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }



    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<SousFormationkeejob>> getByFormation(@PathVariable Long formationId) {
        List<SousFormationkeejob> sousFormations = sousFormationKeejobService.getSousFormationKeejobByFormationKeejob(formationId);
        return ResponseEntity.ok(sousFormations);
    }


    @PostMapping("/assign-logiciels")
    public ResponseEntity<Map<String,String>> assignLogiciels(@RequestBody Map<String,Object> body) {
        Long sousFormationId = Long.valueOf(body.get("sousFormationId").toString());

        List<Integer> logicielsInteger = (List<Integer>) body.get("logiciels");
        List<Long> logicielsIds = logicielsInteger.stream()
                .map(Long::valueOf)
                .toList();

        sousFormationKeejobService.assignLogicielsToSousFormation(sousFormationId, logicielsIds);

        Map<String,String> response = Map.of("message", "Logiciels assignés avec succès");
        return ResponseEntity.ok(response);
    }




}

