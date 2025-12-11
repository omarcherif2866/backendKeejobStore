package com.example.keejobstore.controllers;

import com.example.keejobstore.entity.ServiceFromateur;
import com.example.keejobstore.service.ServiceFormateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/serviceFormateur")
@RequiredArgsConstructor
public class ServiceFormateurController {

    private final ServiceFormateurService serviceFormateurService;

    @GetMapping("/byFormateur/{formateurId}")
    public List<ServiceFromateur> getServiceFormateurByFormateur(@PathVariable Long formateurId) {
        return serviceFormateurService.getServiceFormateurByFormateur(formateurId);
    }

    @PostMapping
    public ResponseEntity<ServiceFromateur> add(@RequestBody ServiceFromateur service) {
        return ResponseEntity.ok(serviceFormateurService.add(service));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceFromateur> update(@PathVariable Long id, @RequestBody ServiceFromateur service) {
        return ResponseEntity.ok(serviceFormateurService.update(id, service));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable Long id) {
        try {
            ServiceFromateur Service = serviceFormateurService.getById(id);
            return ResponseEntity.ok(Service);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}