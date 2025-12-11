package com.example.keejobstore.controllers;

import com.example.keejobstore.entity.*;
import com.example.keejobstore.repository.CVRepository;
import com.example.keejobstore.repository.PartenaireRepository;
import com.example.keejobstore.service.CVService;
import com.example.keejobstore.service.CloudinaryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/cv")
@RequiredArgsConstructor
public class CVController {
    private final CVService cvService;
    private final CloudinaryService cloudinaryService;
    private final PartenaireRepository partenaireRepository;
    private final CVRepository cVRepository;

    @PostMapping
    public ResponseEntity<?> addCv(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("sections") String sectionsJson,
            @RequestParam("priceSections") String priceSectionsJson,  // ← Vérifiez le nom
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds
    ) throws JsonProcessingException {


        ObjectMapper mapper = new ObjectMapper();

        try {
            // Parse sections (avec headline)
            List<CVandLetterSection> sections = mapper.readValue(
                    sectionsJson,
                    new TypeReference<List<CVandLetterSection>>(){}
            );

            // Parse priceSections (avec title)
            List<PriceSection> priceSections = mapper.readValue(
                    priceSectionsJson,
                    new TypeReference<List<PriceSection>>(){}
            );

            CVandLetter cv = new CVandLetter();
            cv.setName(name);
            cv.setDescription(description);
            cv.setSections(sections);
            cv.setPriceSections(priceSections);  // ← Vérifiez que vous appelez bien setPriceSections

            // Upload image si présente
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                cv.setImage(imageUrl);
            }

            // Ajouter partenaires
            if (partenairesIds != null && !partenairesIds.isEmpty()) {
                List<Partenaire> partenaires = partenaireRepository.findAllById(partenairesIds);
                cv.setCvPartenaires(partenaires);
            }

            CVandLetter saved = cVRepository
                    .save(cv);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            System.err.println("❌ ERREUR BACKEND: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }



    @GetMapping("{id}")
    public CVandLetter getCVandLetterById(@PathVariable Long id) {
        return cvService.getCVandLetterById(id);
    }

    @DeleteMapping("{id}")
    public void deleteCVandLetter(@PathVariable Long id) {
        cvService.deleteCVandLetterEntityById(id);
    }

    @GetMapping("/allCVandLetters")
    public ResponseEntity<List<CVandLetter>> getAllCVandLetters() {
        List<CVandLetter> CVandLetters = cvService.getAllCVandLetters();
        return ResponseEntity.ok(CVandLetters);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCVandLetter(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "sections", required = false) String sectionsJson,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds,
            @RequestParam(value = "priceSections", required = false) String priceSectionsJson) {

        try {
            CVandLetter existing = cvService.getCVandLetterById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Évaluation non trouvée");
            }

            if (name != null) existing.setName(name);
            if (description != null) existing.setDescription(description);

            // Mise à jour des sections
            if (sectionsJson != null && !sectionsJson.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                List<CVandLetterSection> sections =
                        mapper.readValue(sectionsJson, new TypeReference<List<CVandLetterSection>>() {});
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
                existing.setCvPartenaires(partenaires);
            }

            CVandLetter saved = cvService.updateCVandLetter(id, existing);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }



}
