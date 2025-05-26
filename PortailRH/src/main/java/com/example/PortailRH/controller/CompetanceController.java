package com.example.PortailRH.controller;

import com.example.PortailRH.entity.Competance;
import com.example.PortailRH.entity.Formation;
import com.example.PortailRH.entity.dto.CompetanceDTO;
import com.example.PortailRH.entity.dto.FormationDTO;
import com.example.PortailRH.service.CompetanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class CompetanceController {
    private final CompetanceService competanceService;

    public CompetanceController(CompetanceService competanceService) {
        this.competanceService = competanceService;
    }
    @PostMapping("/addComp")
    public ResponseEntity<Competance> addCompetance(@RequestBody CompetanceDTO competanceDTO) {
        Competance competance = competanceService.addComprtance(competanceDTO);
        return ResponseEntity.ok(competance);
    }
    @GetMapping("/allCompetance")
    public ResponseEntity<List<Competance>> getAllCompetances() {
        List<Competance> competances = competanceService.getallCompetance();
        return ResponseEntity.ok(competances);
    }
    @PutMapping("Competance/{id}")
    public ResponseEntity<Competance> updateCompetance(@PathVariable Long id, @RequestBody CompetanceDTO competanceDTO) {
        Competance updatedCompetance = competanceService.updateCompetance(id, competanceDTO);
        return ResponseEntity.ok(updatedCompetance);
    }
    @DeleteMapping("competance/{id}")
    public ResponseEntity<Void> deleteCompetance(@PathVariable Long id) {
        competanceService.deleteCompetance(id);
        return ResponseEntity.noContent().build();
    }
}
