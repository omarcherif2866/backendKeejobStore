package com.example.keejobstore.controllers;

import com.example.keejobstore.entity.*;
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
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam("categoryCoaching") String categoryCoachingStr,
            @RequestParam("sections") String sectionsJson,
            @RequestParam("priceSections") String priceSectionsJson,  // ← Vérifiez le nom
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds,
            @RequestParam(value = "iconFiles", required = false) List<MultipartFile> iconFiles,
            @RequestParam(value = "priceIconFiles", required = false) List<MultipartFile> priceIconFiles
    ) throws JsonProcessingException {


        ObjectMapper mapper = new ObjectMapper();

        try {
            // Parse sections (avec headline)
            List<CoachingSection> sections = mapper.readValue(
                    sectionsJson,
                    new TypeReference<List<CoachingSection>>(){}
            );
            CategoryCoaching category;
            try {
                category = CategoryCoaching.valueOf(categoryCoachingStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Catégorie d'évaluation invalide !");
            }
            if (iconFiles != null && !iconFiles.isEmpty()) {
                int iconIndex = 0;

                for (CoachingSection section : sections) {
                    if (section.getDetails() != null && !section.getDetails().isEmpty()) {
                        for (DetailObject detail : section.getDetails()) {
                            if (iconIndex < iconFiles.size()) {
                                MultipartFile iconFile = iconFiles.get(iconIndex);

                                if (iconFile != null && !iconFile.isEmpty() && iconFile.getSize() > 0) {
                                    try {
                                        String iconUrl = cloudinaryService.uploadIcon(iconFile, "icon");
                                        detail.setIcon(iconUrl);
                                        System.out.println("✅ Icon uploaded for section detail '" + detail.getTitre() + "': " + iconUrl);
                                    } catch (Exception e) {
                                        System.err.println("❌ Error uploading icon: " + e.getMessage());
                                        detail.setIcon(null);
                                    }
                                } else {
                                    String existingIcon = detail.getIcon();
                                    if (existingIcon == null || existingIcon.trim().isEmpty()) {
                                        detail.setIcon(null);
                                        System.out.println("ℹ️ No icon for section detail '" + detail.getTitre() + "'");
                                    } else {
                                        System.out.println("ℹ️ Keeping existing icon for section detail '" + detail.getTitre() + "'");
                                    }
                                }
                                iconIndex++;
                            } else {
                                detail.setIcon(null);
                                System.out.println("⚠️ No more iconFiles for section detail '" + detail.getTitre() + "'");
                            }
                        }
                    }
                }
                System.out.println("📊 Total section iconFiles processed: " + iconIndex);
            } else {
                for (CoachingSection section : sections) {
                    if (section.getDetails() != null) {
                        for (DetailObject detail : section.getDetails()) {
                            if (detail.getIcon() == null || detail.getIcon().trim().isEmpty()) {
                                detail.setIcon(null);
                            }
                        }
                    }
                }
            }

            // Parse priceSections (avec title)
            List<PriceSection> priceSections = mapper.readValue(
                    priceSectionsJson,
                    new TypeReference<List<PriceSection>>(){}
            );

            if (priceIconFiles != null && !priceIconFiles.isEmpty()) {
                int priceIconIndex = 0;

                for (PriceSection priceSection : priceSections) {
                    if (priceSection.getDetails() != null && !priceSection.getDetails().isEmpty()) {
                        for (DetailObject detail : priceSection.getDetails()) {
                            if (priceIconIndex < priceIconFiles.size()) {
                                MultipartFile iconFile = priceIconFiles.get(priceIconIndex);

                                if (iconFile != null && !iconFile.isEmpty() && iconFile.getSize() > 0) {
                                    try {
                                        String iconUrl = cloudinaryService.uploadIcon(iconFile, "price-icon");
                                        detail.setIcon(iconUrl);
                                        System.out.println("✅ Price icon uploaded for detail '" + detail.getTitre() + "': " + iconUrl);
                                    } catch (Exception e) {
                                        System.err.println("❌ Error uploading price icon: " + e.getMessage());
                                        detail.setIcon(null);
                                    }
                                } else {
                                    String existingIcon = detail.getIcon();
                                    if (existingIcon == null || existingIcon.trim().isEmpty()) {
                                        detail.setIcon(null);
                                        System.out.println("ℹ️ No icon for price detail '" + detail.getTitre() + "'");
                                    } else {
                                        System.out.println("ℹ️ Keeping existing icon for price detail '" + detail.getTitre() + "'");
                                    }
                                }
                                priceIconIndex++;
                            } else {
                                detail.setIcon(null);
                                System.out.println("⚠️ No more priceIconFiles for detail '" + detail.getTitre() + "'");
                            }
                        }
                    }
                }
                System.out.println("📊 Total price iconFiles processed: " + priceIconIndex);
            } else {
                for (PriceSection priceSection : priceSections) {
                    if (priceSection.getDetails() != null) {
                        for (DetailObject detail : priceSection.getDetails()) {
                            if (detail.getIcon() == null || detail.getIcon().trim().isEmpty()) {
                                detail.setIcon(null);
                            }
                        }
                    }
                }
            }



            CoachingEmploi CoachingEmploi = new CoachingEmploi();
            CoachingEmploi.setName(name);
            CoachingEmploi.setTitre(titre);
            CoachingEmploi.setSousTitre(sousTitre);
            CoachingEmploi.setDescription(description);
            CoachingEmploi.setSections(sections);
            CoachingEmploi.setPriceSections(priceSections);  // ← Vérifiez que vous appelez bien setPriceSections
            CoachingEmploi.setCategoryCoaching(category);

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

            if (logo != null && !logo.isEmpty()) {
                String logoUrl = cloudinaryService.uploadImage(logo);
                CoachingEmploi.setLogo(logoUrl);
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
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam("categoryCoaching") String categoryCoachingStr,
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds,
            @RequestParam(value = "priceSections", required = false) String priceSectionsJson,
            @RequestParam(value = "iconFiles", required = false) List<MultipartFile> iconFiles,
            @RequestParam(value = "priceIconFiles", required = false) List<MultipartFile> priceIconFiles) {

        try {
            CoachingEmploi existing = coachingService.getCoachingEmploiById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Évaluation non trouvée");
            }

            if (name != null) existing.setName(name);
            if (titre != null) existing.setTitre(titre);
            if (sousTitre != null) existing.setSousTitre(sousTitre);

            if (description != null) existing.setDescription(description);

            CategoryCoaching category;
            try {
                category = CategoryCoaching.valueOf(categoryCoachingStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Catégorie d'évaluation invalide !");
            }
            existing.setCategoryCoaching(category);
            
            // Mise à jour des sections
            ObjectMapper mapper = new ObjectMapper();
            List<CoachingSection> sections =
                    mapper.readValue(sectionsJson, new TypeReference<List<CoachingSection>>() {});

            // Traitement des iconFiles pour sections
            if (iconFiles != null && !iconFiles.isEmpty()) {
                int iconIndex = 0;

                for (CoachingSection section : sections) {
                    if (section.getDetails() != null && !section.getDetails().isEmpty()) {
                        for (DetailObject detail : section.getDetails()) {
                            if (iconIndex < iconFiles.size()) {
                                MultipartFile iconFile = iconFiles.get(iconIndex);

                                if (iconFile != null && !iconFile.isEmpty() && iconFile.getSize() > 0) {
                                    try {
                                        String iconUrl = cloudinaryService.uploadIcon(iconFile, "icon");
                                        detail.setIcon(iconUrl);
                                        System.out.println("✅ Section icon updated for detail '" + detail.getTitre() + "': " + iconUrl);
                                    } catch (Exception e) {
                                        System.err.println("❌ Error uploading section icon: " + e.getMessage());
                                    }
                                } else {
                                    String existingIcon = detail.getIcon();
                                    if (existingIcon == null || existingIcon.trim().isEmpty()) {
                                        detail.setIcon(null);
                                        System.out.println("ℹ️ No icon for section detail '" + detail.getTitre() + "'");
                                    } else {
                                        System.out.println("ℹ️ Keeping existing section icon for detail '" + detail.getTitre() + "'");
                                    }
                                }
                                iconIndex++;
                            }
                        }
                    }
                }
                System.out.println("📊 UPDATE - Total section iconFiles processed: " + iconIndex);
            } else {
                for (CoachingSection section : sections) {
                    if (section.getDetails() != null) {
                        for (DetailObject detail : section.getDetails()) {
                            if (detail.getIcon() == null || detail.getIcon().trim().isEmpty()) {
                                detail.setIcon(null);
                            }
                        }
                    }
                }
            }

            if (priceSectionsJson != null && !priceSectionsJson.isEmpty()) {
                ObjectMapper priceMapper = new ObjectMapper();
                List<PriceSection> priceSections =
                        priceMapper.readValue(priceSectionsJson, new TypeReference<List<PriceSection>>() {});

                // Traitement des priceIconFiles pour priceSections
                if (priceIconFiles != null && !priceIconFiles.isEmpty()) {
                    int priceIconIndex = 0;

                    for (PriceSection priceSection : priceSections) {
                        if (priceSection.getDetails() != null && !priceSection.getDetails().isEmpty()) {
                            for (DetailObject detail : priceSection.getDetails()) {
                                if (priceIconIndex < priceIconFiles.size()) {
                                    MultipartFile iconFile = priceIconFiles.get(priceIconIndex);

                                    if (iconFile != null && !iconFile.isEmpty() && iconFile.getSize() > 0) {
                                        try {
                                            String iconUrl = cloudinaryService.uploadIcon(iconFile, "price-icon");
                                            detail.setIcon(iconUrl);
                                            System.out.println("✅ Price icon updated for detail '" + detail.getTitre() + "': " + iconUrl);
                                        } catch (Exception e) {
                                            System.err.println("❌ Error uploading price icon: " + e.getMessage());
                                        }
                                    } else {
                                        String existingIcon = detail.getIcon();
                                        if (existingIcon == null || existingIcon.trim().isEmpty()) {
                                            detail.setIcon(null);
                                            System.out.println("ℹ️ No icon for price detail '" + detail.getTitre() + "'");
                                        } else {
                                            System.out.println("ℹ️ Keeping existing price icon for detail '" + detail.getTitre() + "'");
                                        }
                                    }
                                    priceIconIndex++;
                                }
                            }
                        }
                    }
                    System.out.println("📊 UPDATE - Total price iconFiles processed: " + priceIconIndex);
                } else {
                    for (PriceSection priceSection : priceSections) {
                        if (priceSection.getDetails() != null) {
                            for (DetailObject detail : priceSection.getDetails()) {
                                if (detail.getIcon() == null || detail.getIcon().trim().isEmpty()) {
                                    detail.setIcon(null);
                                }
                            }
                        }
                    }
                }

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

            if (logo != null && !logo.isEmpty()) {
                String logoUrl = cloudinaryService.uploadImage(logo);
                existing.setLogo(logoUrl);
            }
            
            CoachingEmploi saved = coachingService.updateCoachingEmploi(id, existing);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }



}
