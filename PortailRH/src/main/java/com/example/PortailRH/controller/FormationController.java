package com.example.PortailRH.controller;

import com.example.PortailRH.entity.Formation;
import com.example.PortailRH.entity.Reclamation;
import com.example.PortailRH.entity.dto.FormationDTO;
import com.example.PortailRH.entity.dto.ReclamationDTO;
import com.example.PortailRH.service.FormationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class FormationController {
    private final FormationService formationService;

    public FormationController(FormationService formationService) {
        this.formationService = formationService;
    }
    @PostMapping("/addF")
    public ResponseEntity<Formation> addFormation(@RequestBody FormationDTO formationDTO) {
        Formation formation = formationService.addFormation(formationDTO);
        return ResponseEntity.ok(formation);
    }
    @GetMapping("/allFormation")
    public ResponseEntity<List<Formation>> getAllRFormations() {
        List<Formation> formations = formationService.getall();
        return ResponseEntity.ok(formations);
    }

    @GetMapping("Formation/{id}")
    public ResponseEntity<Formation> getFormation(@PathVariable Long id) {
        Optional<Formation> formation = formationService.getFormationByID(id);
        return formation.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("formation/{id}")
    public ResponseEntity<Formation> updateFormation(@PathVariable Long id, @RequestBody FormationDTO formationDTO) {
        Formation updatedFormation = formationService.updateFormation(id, formationDTO);
        return ResponseEntity.ok(updatedFormation);
    }
    @GetMapping("/form/{userId}")
    public List<Formation> getFormationByUserId(@PathVariable Long userId) {
        return formationService.findByUserId(userId);
    }
    @GetMapping("/withusernames")
    public List<String> getAllFormationsWithUsernames() {
        return formationService.getAllFormationsWithUsernames();
    }
}
