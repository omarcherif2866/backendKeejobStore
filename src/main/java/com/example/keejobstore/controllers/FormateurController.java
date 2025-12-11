package com.example.keejobstore.controllers;

import com.example.keejobstore.entity.Formateur;
import com.example.keejobstore.service.CloudinaryService;
import com.example.keejobstore.service.FormateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/formateur")
@RequiredArgsConstructor
public class FormateurController {
    private final FormateurService formateurService;
    private final CloudinaryService cloudinaryService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addFormateur(
            @RequestParam("address") String address,
            @RequestParam("description") String description,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("experience") String experience,
            @RequestParam("poste") String poste,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("university") String university,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            // Validation
            if (address == null || address.isEmpty() || description == null || description.isEmpty() ||
                    email == null || email.isEmpty() || phone == null || phone.isEmpty() ||
                    experience == null || experience.isEmpty() || poste == null || poste.isEmpty() ||
                    firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() ||
                    university == null || university.isEmpty()) {
                return ResponseEntity.badRequest().body("Paramètres d'entrée invalides.");
            }

            Formateur formateur = new Formateur();
            formateur.setAddress(address);
            formateur.setDescription(description);
            formateur.setEmail(email);
            formateur.setPhone(phone);
            formateur.setExperience(experience);
            formateur.setPoste(poste);
            formateur.setFirstName(firstName);
            formateur.setLastName(lastName);
            formateur.setUniversity(university);

            // Upload de l'image vers Cloudinary
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                formateur.setImage(imageUrl);
            }

            Formateur savedFormateur = formateurService.addFormateur(formateur);

            return ResponseEntity.ok(savedFormateur);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload de l'image : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur s'est produite lors du traitement de la demande : " + e.getMessage());
        }
    }


    @GetMapping("{id}")
    public Formateur getFormateurById(@PathVariable Long id) {
        return formateurService.getFormateurById(id);
    }

    @DeleteMapping("{id}")
    public void deleteFormateur(@PathVariable Long id) {
        formateurService.deleteFormateurEntityById(id);
    }

    @GetMapping("/allFormateurs")
    public ResponseEntity<List<Formateur>> getAllFormateurs() {
        List<Formateur> Formateurs = formateurService.getAllFormateurs();
        return ResponseEntity.ok(Formateurs);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateFormateur(
            @PathVariable Long id,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("description") String description,
            @RequestParam("experience") String experience,
            @RequestParam("poste") String poste,
            @RequestParam("university") String university,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            Formateur existing = formateurService.getFormateurById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Formateur non trouvé");
            }

            // MAJ des champs
            existing.setFirstName(firstName);
            existing.setLastName(lastName);
            existing.setEmail(email);
            existing.setPhone(phone);
            existing.setAddress(address);
            existing.setDescription(description);
            existing.setExperience(experience);
            existing.setPoste(poste);
            existing.setUniversity(university);

            // Si image présente
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                existing.setImage(imageUrl);
            }

            Formateur saved = formateurService.updateFormateur(id, existing);
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



