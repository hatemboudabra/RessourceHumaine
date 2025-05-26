package com.example.PortailRH.service;

import com.example.PortailRH.entity.Evaluation;
import com.example.PortailRH.entity.Tache;
import com.example.PortailRH.entity.Team;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.EvaluationDTO;
import com.example.PortailRH.repository.EvaluationRepo;
import com.example.PortailRH.repository.TacheRepo;
import com.example.PortailRH.repository.TeamRepo;
import com.example.PortailRH.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EvaluationServiceImpl implements EvaluationService {
    private final EvaluationRepo evaluationRepo;
    private final UserRepo userRepo;
    private final TacheRepo tacheRepo;
    private final TeamRepo teamRepo;

    public EvaluationServiceImpl(EvaluationRepo evaluationRepo, UserRepo userRepo, TacheRepo tacheRepo, TeamRepo teamRepo) {
        this.evaluationRepo = evaluationRepo;
        this.userRepo = userRepo;
        this.tacheRepo = tacheRepo;
        this.teamRepo = teamRepo;
    }

    @Override
    public Evaluation addEvaluation(EvaluationDTO evaluationDTO) {
        Evaluation evaluation = new Evaluation();
        evaluation.setComment(evaluationDTO.getComment());
        evaluation.setNote(evaluationDTO.getNote());
        User user = userRepo.findById(Math.toIntExact(evaluationDTO.getUserId())).get();
        evaluation.setUser(user);
        return evaluationRepo.save(evaluation);
    }

    @Override
    public Evaluation updateEvaluation(Long id, EvaluationDTO evaluationDTO) {
        Evaluation evaluation = evaluationRepo.findById(id).orElseThrow(() -> new RuntimeException(
                "Evaluation not found with id:" + id
        ));
        evaluation.setComment(evaluationDTO.getComment());
        evaluation.setNote(evaluationDTO.getNote());
        User user = userRepo.findById(Math.toIntExact(evaluationDTO.getUserId())).get();
        evaluation.setUser(user);
        return evaluationRepo.save(evaluation);
    }

    @Override
    public List<Evaluation> getallEvaluation() {
        return evaluationRepo.findAll();
    }

    @Override
    public Optional<Evaluation> getEvaluationById(Long id) {
        return evaluationRepo.findById(id);
    }

    @Override
    public void deleteEvaluation(Long id) {
        evaluationRepo.deleteById(id);
    }

    @Override
    public void evaluateUser(Long chefId, Long collaboratorId) {
        User chef = userRepo.findById(Math.toIntExact(chefId))
                .orElseThrow(() -> new RuntimeException("Chef not found"));
        User collaborator = userRepo.findById(Math.toIntExact(collaboratorId))
                .orElseThrow(() -> new RuntimeException("Collaborator not found"));
        List<Team> teams = teamRepo.findByChef(chef);
        if (teams.isEmpty()) {
            throw new RuntimeException("Chef does not manage any team");
        }
        boolean isCollaboratorInTeam = teams.stream()
                .anyMatch(team -> team.getCollaborators().contains(collaborator));

        if (!isCollaboratorInTeam) {
            throw new RuntimeException("The collaborator does not belong to the same team as the chef");
        }
        List<Tache> taches = tacheRepo.findByUser(collaborator);
        if (taches.isEmpty()) {
            throw new RuntimeException("No tasks assigned to this collaborator");
        }
        double totalPoints = 0.0;
        for (Tache tache : taches) {
            switch (tache.getComplexite()) {
                case SIMPLE:
                    totalPoints += 1;
                    break;
                case INTERMEDIAIRE:
                    totalPoints += 2;
                    break;
                case AVANCEE:
                    totalPoints += 3;
                    break;
            }
        }
        double moyenne = totalPoints / taches.size();
        collaborator.setNoteGlobale(moyenne);
        userRepo.save(collaborator);
        Evaluation evaluation = new Evaluation();
        evaluation.setUser(collaborator);
        evaluation.setComment("Évaluation basée sur les tâches assignées par le chef d'équipe");
        evaluation.setNote((long) moyenne);
        evaluationRepo.save(evaluation);
    }

    public String getCollaboratorEvaluationNote(Long collaboratorId) {
        User collaborator = userRepo.findById(Math.toIntExact(collaboratorId))
                .orElseThrow(() -> new RuntimeException("Collaborator not found"));

        List<Evaluation> evaluations = evaluationRepo.findByUser(collaborator);

        if (evaluations.isEmpty()) {
            return " ";
        } else {
            Evaluation lastEvaluation = evaluations.get(evaluations.size() - 1);
            return String.valueOf(lastEvaluation.getNote());  }
    }

    @Override

    public List<Map<String, Object>> getAllUsersWithAverageNote() {
        List<Object[]> results = evaluationRepo.findAverageNoteForAllUsers();
        List<Map<String, Object>> userNotes = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", (String) result[0]);
            userData.put("averageNote", (Double) result[1]);
            userNotes.add(userData);
        }

        return userNotes;


    }
}