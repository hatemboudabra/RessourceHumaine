package com.example.PortailRH.controller;

import com.example.PortailRH.entity.Demande;
import com.example.PortailRH.entity.dto.DemandeDTO;
import com.example.PortailRH.entity.enummerations.Status;
import com.example.PortailRH.service.DemandeSerice;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class DemandeController {
    private final DemandeSerice demandeSerice;

    public DemandeController(DemandeSerice demandeSerice) {
        this.demandeSerice = demandeSerice;
    }

    @PostMapping
    public ResponseEntity<Demande> addDemande(@RequestBody DemandeDTO demandeDTO) {
        Demande demande = demandeSerice.addDemande(demandeDTO);
        return ResponseEntity.ok(demande);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Demande> updateDemande(
            @PathVariable Long id,
            @RequestBody DemandeDTO demandeDTO
    ) {
        Demande updatedDemande = demandeSerice.UpdateDemande(id, demandeDTO);
        return ResponseEntity.ok(updatedDemande);
    }
    @GetMapping
    public ResponseEntity<List<Demande>> getAllDemandes() {
        List<Demande> demandes = demandeSerice.getallDemande();
        return ResponseEntity.ok(demandes);
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<Demande> changeStatus(@PathVariable Long id, @RequestParam Status newStatus) {
        try {
            Demande updatedDemande = demandeSerice.changeStatus(id, newStatus);
            return ResponseEntity.ok(updatedDemande);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Demande> getDemandeById(@PathVariable Long id) {
        Optional<Demande> demande = demandeSerice.getById(id);
        return demande.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PatchMapping("/{id}/status/prêt-ou-avance")
    public ResponseEntity<Demande> changeStatusForLoanOrAdvance(@PathVariable Long id, @RequestParam Status newStatus) {
        try {
            Demande updatedDemande = demandeSerice.changeStatusForLoanOrAdvance(id, newStatus);
            return ResponseEntity.ok(updatedDemande);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/{id}/status/document-ou-formation-ou-congé")
    public ResponseEntity<Demande> changeStatusForDocumentOrTrainingOrLeave(@PathVariable Long id, @RequestParam Status newStatus) {
        try {
            Demande updatedDemande = demandeSerice.changeStatusForDocumentOrTrainingOrLeave(id, newStatus);
            return ResponseEntity.ok(updatedDemande);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/loan-advance")
    public ResponseEntity<List<Demande>> getLoanOrAdvanceRequests() {
        List<Demande> demandes = demandeSerice.getLoanOrAdvanceRequests();
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/document-training-leave")
    public ResponseEntity<List<Demande>> getDocumentTrainingOrLeaveRequests() {
        List<Demande> demandes = demandeSerice.getDocumentTrainingOrLeaveRequests();
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/user/{userId}")
    public List<Demande> getDemandesByUser(@PathVariable Long userId) {
        return demandeSerice.getDemandesByUserId(userId);
    }
  /*  @GetMapping("/equipe/{chefId}")
    public ResponseEntity<List<Demande>> getDemandesEquipeChef(@PathVariable Long chefId) {
        List<Demande> demandes = demandeSerice.getDemandesEquipeChef(chefId);
        return ResponseEntity.ok(demandes);
    }*/

    @GetMapping("/countchef")
    public ResponseEntity<Map<Status, Long>> getDemandesCountByStatusForDocumentTrainingOrLeave() {
        Map<Status, Long> counts = demandeSerice.getDemandesCountByStatusForDocumentTrainingOrLeave();
        return ResponseEntity.ok(counts);
    }
    @GetMapping("/countRh")
    public ResponseEntity<Map<Status, Long>> getDemandesCountByStatusForLeave() {
        Map<Status,Long> count = demandeSerice.getDemandesCountByStatusForLoanOrAdvanceRequest();
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("demandes/{id}")
    public ResponseEntity<Void> deleteDemande(@PathVariable Long id) {
        demandeSerice.DeleteDemande(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/stats/mois")
    public Map<String, Long> getDemandesCountByMonth() {
        return demandeSerice.getDemandesCountByMonth();
    }
    }
