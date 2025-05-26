package com.example.PortailRH.controller;

import com.example.PortailRH.entity.Projet;
import com.example.PortailRH.entity.dto.ProjectDTO;
import com.example.PortailRH.service.ProjetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ProjetController {
    private final ProjetService projetService;

    public ProjetController(ProjetService projetService) {
        this.projetService = projetService;
    }

    @GetMapping("/allProjects")
    public List<Projet> getAllProjects() {
       return projetService.getAllProjet();
    }
    @PostMapping("/addProject")
    public Projet addProject(@RequestBody ProjectDTO projectDTO) {
        return projetService.addProjet(projectDTO);

    }

    @GetMapping("project/{chefId}")
    public List<Projet> getProjectById(@PathVariable Long chefId) {
        return projetService.findByChefId(chefId);
    }
    @DeleteMapping("projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projetService.deleteProjetById(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("project/{id}")
    public ResponseEntity<Projet> updateProject(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
     projetService.updateProjet(id, projectDTO);
     return ResponseEntity.noContent().build();
    }
}
