package com.example.PortailRH.controller;


import com.example.PortailRH.entity.Evaluation;
import com.example.PortailRH.entity.dto.EvaluationDTO;
import com.example.PortailRH.service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class EvaluationController {
    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }
    @PostMapping("/addEvaluation")
    public ResponseEntity<Evaluation> addEvaluation(@RequestBody EvaluationDTO evaluationDTO){
        Evaluation evaluation = evaluationService.addEvaluation(evaluationDTO);
        return  ResponseEntity.ok(evaluation);
    }
       @GetMapping("/getallEvaluation")
    public ResponseEntity<List<Evaluation>> getallEvaluation(){
        List<Evaluation> evaluations = evaluationService.getallEvaluation();
        return ResponseEntity.ok(evaluations);
    }
    @GetMapping("Evaluation/{id}")
    public ResponseEntity<Evaluation> getEvaluationById(@PathVariable Long id){
        Optional<Evaluation> evaluation = evaluationService.getEvaluationById(id);
        return  evaluation.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("Evaluation/{id}")
    public ResponseEntity<Evaluation> updateEvaluation(@PathVariable Long id , @RequestBody EvaluationDTO evaluationDTO){
        Evaluation evaluation = evaluationService.updateEvaluation(id,evaluationDTO);
        return ResponseEntity.ok(evaluation);
    }
    @DeleteMapping("Evaluation/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id){
        evaluationService.deleteEvaluation(id);
        return  ResponseEntity.noContent().build();
    }
    @PostMapping("/evaluateUser")
    public ResponseEntity<String> evaluateUser(
            @RequestParam Long chefId,
            @RequestParam Long collaboratorId) {
        try {
            evaluationService.evaluateUser(chefId, collaboratorId);
            return ResponseEntity.ok("Collaborator evaluated successfully.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/collaborator/{collaboratorId}")
    public String getCollaboratorEvaluationNote(@PathVariable Long collaboratorId) {
        return evaluationService.getCollaboratorEvaluationNote(collaboratorId);
    }
    @GetMapping("/all-users-notes")
    public List<Map<String, Object>> getAllUsersWithAverageNote() {
        return evaluationService.getAllUsersWithAverageNote();
    }

}
