package com.example.keejobstore.controllers;

import com.example.keejobstore.entity.CoachingEmploi;
import com.example.keejobstore.entity.CoachingSection;
import com.example.keejobstore.entity.Partenaire;
import com.example.keejobstore.entity.PriceSection;
import com.example.keejobstore.repository.CoachingRepository;
import com.example.keejobstore.repository.PartenaireRepository;
import com.example.keejobstore.service.CloudinaryService;
import com.example.keejobstore.service.CoachingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coaching")
@RequiredArgsConstructor
public class CoachingController {

    private final CoachingService coachingService;
    private final CloudinaryService cloudinaryService;
    private final PartenaireRepository partenaireRepository;
    private final CoachingRepository coachingRepository;



    @PostMapping
    public ResponseEntity<?> addCoachingEmploi(
            @RequestParam("name") String name,
            @RequestParam("titre") String titre,
            @RequestParam("sousTitre") String sousTitre,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("sections") String sectionsJson,
            @RequestParam("priceSections") String priceSectionsJson,  // ← Vérifiez le nom
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds
    ) throws JsonProcessingException {


        ObjectMapper mapper = new ObjectMapper();

        try {
            // Parse sections (avec headline)
            List<CoachingSection> sections = mapper.readValue(
                    sectionsJson,
                    new TypeReference<List<CoachingSection>>(){}
            );

            // Parse priceSections (avec title)
            List<PriceSection> priceSections = mapper.readValue(
                    priceSectionsJson,
                    new TypeReference<List<PriceSection>>(){}
            );

            CoachingEmploi CoachingEmploi = new CoachingEmploi();
            CoachingEmploi.setName(name);
            CoachingEmploi.setTitre(titre);
            CoachingEmploi.setSousTitre(sousTitre);
            CoachingEmploi.setDescription(description);
            CoachingEmploi.setSections(sections);
            CoachingEmploi.setPriceSections(priceSections);  // ← Vérifiez que vous appelez bien setPriceSections

            // Upload image si présente
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                CoachingEmploi.setImage(imageUrl);
            }

            // Ajouter partenaires
            if (partenairesIds != null && !partenairesIds.isEmpty()) {
                List<Partenaire> partenaires = partenaireRepository.findAllById(partenairesIds);
                CoachingEmploi.setCoachingPartenaires(partenaires);
            }

            CoachingEmploi saved = coachingRepository
                    .save(CoachingEmploi);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            System.err.println("❌ ERREUR BACKEND: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }



    @GetMapping("{id}")
    public CoachingEmploi getCoachingEmploiById(@PathVariable Long id) {
        return coachingService.getCoachingEmploiById(id);
    }

    @DeleteMapping("{id}")
    public void deleteCoachingEmploi(@PathVariable Long id) {
        coachingService.deleteCoachingEmploiEntityById(id);
    }

    @GetMapping("/allCoachingEmplois")
    public ResponseEntity<List<CoachingEmploi>> getAllCoachingEmplois() {
        List<CoachingEmploi> CoachingEmplois = coachingService.getAllCoachingEmplois();
        return ResponseEntity.ok(CoachingEmplois);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCoachingEmploi(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "titre", required = false) String titre,
            @RequestParam(value = "sousTitre", required = false) String sousTitre,

            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "sections", required = false) String sectionsJson,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds,
            @RequestParam(value = "priceSections", required = false) String priceSectionsJson) {

        try {
            CoachingEmploi existing = coachingService.getCoachingEmploiById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Évaluation non trouvée");
            }

            if (name != null) existing.setName(name);
            if (titre != null) existing.setTitre(titre);
            if (sousTitre != null) existing.setSousTitre(sousTitre);

            if (description != null) existing.setDescription(description);

            // Mise à jour des sections
            if (sectionsJson != null && !sectionsJson.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                List<CoachingSection> sections =
                        mapper.readValue(sectionsJson, new TypeReference<List<CoachingSection>>() {});
                existing.setSections(sections);
            }

            if (priceSectionsJson != null && !priceSectionsJson.isEmpty()) {
                ObjectMapper priceMapper = new ObjectMapper();
                List<PriceSection> priceSections =
                        priceMapper.readValue(priceSectionsJson, new TypeReference<List<PriceSection>>() {});
                existing.setPriceSections(priceSections);
            }

            // Mise à jour image principale
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                existing.setImage(imageUrl);
            }

            // Mise à jour partenaires
            if (partenairesIds != null) {
                List<Partenaire> partenaires = partenaireRepository.findAllById(partenairesIds);
                existing.setCoachingPartenaires(partenaires);
            }

            CoachingEmploi saved = coachingService.updateCoachingEmploi(id, existing);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }



}
