package com.example.PortailRH.controller;

import com.example.PortailRH.entity.Demande;
import com.example.PortailRH.entity.Reclamation;
import com.example.PortailRH.entity.dto.DemandeDTO;
import com.example.PortailRH.entity.dto.ReclamationDTO;
import com.example.PortailRH.service.ReclamationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class ReclamationController {
    private final ReclamationService reclamationService;

    public ReclamationController(ReclamationService reclamationService) {
        this.reclamationService = reclamationService;

    }

    @PostMapping("/add")
    public ResponseEntity<Reclamation> addReclamation(@RequestBody ReclamationDTO reclamationDTO) {
        Reclamation reclamation = reclamationService.addReclamation(reclamationDTO);
        return ResponseEntity.ok(reclamation);
    }
    @GetMapping("/allRec")
    public ResponseEntity<List<Reclamation>> getAllReclamations() {
        List<Reclamation> reclamations = reclamationService.getallReclamation();
        return ResponseEntity.ok(reclamations);
    }

    @GetMapping("Rec/{id}")
    public ResponseEntity<Reclamation> getReclamation(@PathVariable Long id) {
        Optional<Reclamation> reclamationOptional = reclamationService.getById(id);
        return reclamationOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("Recl/{id}")
    public ResponseEntity<Void> deleteReclamation(@PathVariable Long id) {
       reclamationService.DeleteReclamation(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("claims/{id}")
    public List<Reclamation> getReclamationsByUser(@PathVariable Long id) {
        return reclamationService.findByUserId(id);
    }
}
