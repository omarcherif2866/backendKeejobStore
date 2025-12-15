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
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam("categoryCV") String categoryCVStr,
            @RequestParam("sections") String sectionsJson,
            @RequestParam("priceSections") String priceSectionsJson,
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds,
            @RequestParam(value = "iconFiles", required = false) List<MultipartFile> iconFiles,
            @RequestParam(value = "priceIconFiles", required = false) List<MultipartFile> priceIconFiles) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        try {
            // Parse sections (avec headline)
            List<CVandLetterSection> sections = mapper.readValue(
                    sectionsJson,
                    new TypeReference<List<CVandLetterSection>>(){}
            );

            CategoryCV category;
            try {
                category = CategoryCV.valueOf(categoryCVStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Catégorie d'évaluation invalide !");
            }

            // Parse priceSections (avec title)
            List<PriceSection> priceSections = mapper.readValue(
                    priceSectionsJson,
                    new TypeReference<List<PriceSection>>(){}
            );

            // ========== TRAITEMENT DES ICONS POUR SECTIONS ==========
            if (iconFiles != null && !iconFiles.isEmpty()) {
                int iconIndex = 0;

                for (CVandLetterSection section : sections) {
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
                for (CVandLetterSection section : sections) {
                    if (section.getDetails() != null) {
                        for (DetailObject detail : section.getDetails()) {
                            if (detail.getIcon() == null || detail.getIcon().trim().isEmpty()) {
                                detail.setIcon(null);
                            }
                        }
                    }
                }
            }

            // ========== TRAITEMENT DES ICONS POUR PRICE SECTIONS ==========
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

            // ========== CRÉATION ET SAUVEGARDE DU CV ==========
            CVandLetter cv = new CVandLetter();
            cv.setName(name);
            cv.setDescription(description);
            cv.setSections(sections);
            cv.setPriceSections(priceSections);
            cv.setCategoryCV(category);

            // Upload image si présente
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                cv.setImage(imageUrl);
            }

            if (logo != null && !logo.isEmpty()) {
                String logoUrl = cloudinaryService.uploadImage(logo);
                cv.setLogo(logoUrl);
            }

            // Ajouter partenaires
            if (partenairesIds != null && !partenairesIds.isEmpty()) {
                List<Partenaire> partenaires = partenaireRepository.findAllById(partenairesIds);
                cv.setCvPartenaires(partenaires);
            }

            CVandLetter saved = cVRepository.save(cv);
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
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam("categoryCV") String categoryCVStr,
            @RequestParam(value = "partenairesIds", required = false) List<Long> partenairesIds,
            @RequestParam(value = "priceSections", required = false) String priceSectionsJson,
            @RequestParam(value = "iconFiles", required = false) List<MultipartFile> iconFiles,
            @RequestParam(value = "priceIconFiles", required = false) List<MultipartFile> priceIconFiles) {

        try {
            CVandLetter existing = cvService.getCVandLetterById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Évaluation non trouvée");
            }

            CategoryCV category;
            try {
                category = CategoryCV.valueOf(categoryCVStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Catégorie d'évaluation invalide !");
            }
            existing.setCategoryCV(category);

            if (name != null) existing.setName(name);
            if (description != null) existing.setDescription(description);

            // ========== MISE À JOUR DES SECTIONS ==========
            ObjectMapper mapper = new ObjectMapper();
            List<CVandLetterSection> sections =
                    mapper.readValue(sectionsJson, new TypeReference<List<CVandLetterSection>>() {});

            // Traitement des iconFiles pour sections
            if (iconFiles != null && !iconFiles.isEmpty()) {
                int iconIndex = 0;

                for (CVandLetterSection section : sections) {
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
                for (CVandLetterSection section : sections) {
                    if (section.getDetails() != null) {
                        for (DetailObject detail : section.getDetails()) {
                            if (detail.getIcon() == null || detail.getIcon().trim().isEmpty()) {
                                detail.setIcon(null);
                            }
                        }
                    }
                }
            }

            existing.setSections(sections);

            // ========== MISE À JOUR DES PRICE SECTIONS ==========
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

            // ========== MISE À JOUR IMAGE ET LOGO ==========
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                existing.setImage(imageUrl);
            }

            if (logo != null && !logo.isEmpty()) {
                String logoUrl = cloudinaryService.uploadImage(logo);
                existing.setLogo(logoUrl);
            }

            // ========== MISE À JOUR PARTENAIRES ==========
            if (partenairesIds != null) {
                List<Partenaire> partenaires = partenaireRepository.findAllById(partenairesIds);
                existing.setCvPartenaires(partenaires);
            }

            CVandLetter saved = cvService.updateCVandLetter(id, existing);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            System.err.println("❌ ERREUR UPDATE: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }


    @GetMapping("/by-category/{category}")
    public ResponseEntity<?> getCVByCategory(@PathVariable String category) {
        try {
            CategoryCV enumValue = CategoryCV.valueOf(category);
            List<CVandLetter> cvs = cvService.findByCategoryCV(enumValue);
            return ResponseEntity.ok(cvs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Catégorie invalide !");
        }
    }


}
