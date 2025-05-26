package com.example.PortailRH.service;

import com.example.PortailRH.entity.Evaluation;
import com.example.PortailRH.entity.dto.EvaluationDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EvaluationService {
    public Evaluation addEvaluation(EvaluationDTO evaluationDTO);
    public Evaluation updateEvaluation (Long id , EvaluationDTO evaluationDTO);
    public List<Evaluation> getallEvaluation();
    public Optional<Evaluation> getEvaluationById(Long id);
    public void deleteEvaluation(Long id);
    public void evaluateUser(Long chefId, Long collaboratorId);
    public String getCollaboratorEvaluationNote(Long collaboratorId);
    public List<Map<String, Object>> getAllUsersWithAverageNote();

}
