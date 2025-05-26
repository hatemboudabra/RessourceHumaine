package com.example.PortailRH.controller;

import com.example.PortailRH.entity.Tache;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.TacheDTO;
import com.example.PortailRH.entity.enummerations.StatusTache;
import com.example.PortailRH.service.TacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class TacheController {
    private final TacheService tacheService;

    public TacheController(TacheService tacheService) {
        this.tacheService = tacheService;
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<Tache> updateStatus(@PathVariable Long id, @RequestParam StatusTache newStatus) {
        Tache updatedTache = tacheService.updateStatus(id, newStatus);
        return ResponseEntity.ok(updatedTache);
    }

    @PostMapping("/add-by-chef/{chefId}")
    public ResponseEntity<Tache> addTacheByChef(
            @PathVariable Long chefId,
            @RequestBody TacheDTO tacheDTO) {
        try {
            Tache tache = tacheService.addTacheByChef(chefId, tacheDTO);
            return ResponseEntity.ok(tache);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("/allTache")
    public ResponseEntity<List<Tache>> getAllTaches() {
        List<Tache> taches = tacheService.getallTache();
        return ResponseEntity.ok(taches);
    }

    @GetMapping("Tache/{id}")
    public ResponseEntity<Tache> getRTache(@PathVariable Long id) {
        Optional<Tache> tache = tacheService.getTacheById(id);
        return tache.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{tacheId}/assign")
    public ResponseEntity<Tache> assignTacheToCollaborator(
            @PathVariable Long tacheId,
            @RequestParam Long chefId,
            @RequestParam Long collaboratorId) {
        Tache assignedTache = tacheService.assignTacheToCollaborator(tacheId, chefId, collaboratorId);

        return ResponseEntity.ok(assignedTache);
    }
    @GetMapping("/by-chef/{chefId}")
    public List<Tache> getTachesByChef(@PathVariable Long chefId) {
        return tacheService.gettacheByChef(chefId);
    }

    @GetMapping("/chef/{chefId}/collaborator/{collaboratorId}/taches")
    public ResponseEntity<List<Tache>> getAssignedTachesByChef(
            @PathVariable Long chefId,
            @PathVariable Long collaboratorId) {
        List<Tache> taches = tacheService.getAssignedTachesByChef(chefId, collaboratorId);
        return ResponseEntity.ok(taches);
    }
    @GetMapping("/{tacheId}/assigned-nameCola")
    public ResponseEntity<String> getAssignedCollaborator(@PathVariable Long tacheId) {
        String username = tacheService.getAssignedCollaboratorUsername(tacheId);
        return ResponseEntity.ok(username);
    }
       @GetMapping("/stats/{chefId}")
       public ResponseEntity<Map<String, Long>> getTacheStatsByChef(@PathVariable Long chefId) {
           Map<StatusTache, Long> stats = tacheService.countTachesByStatusAndChef(chefId);

           Map<String, Long> statsString = new HashMap<>();
           stats.forEach((key, value) -> statsString.put(key.name(), value));

           return ResponseEntity.ok(statsString);
    }
    @GetMapping("/chef/{chefId}/collaborateurs/stats")
    public ResponseEntity<Map<String, Long>> getTachesAssignedByChefToCollaborators(@PathVariable Long chefId) {
        Map<String, Long> stats = tacheService.countTachesAssignedByChefToCollaborators(chefId);
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/collaborateur/{collaboratorId}")
    public ResponseEntity<List<Tache>> getTachesByCollaboratorId(@PathVariable Long collaboratorId) {
        List<Tache> taches = tacheService.getTachesByCollaboratorId(collaboratorId);
        return ResponseEntity.ok(taches);
    }


    @DeleteMapping("tasks/{id}")
    public ResponseEntity<Void> deleteTasks(@PathVariable Long id) {
        tacheService.DeleteTache(id);
        return ResponseEntity.noContent().build();
    }
}
