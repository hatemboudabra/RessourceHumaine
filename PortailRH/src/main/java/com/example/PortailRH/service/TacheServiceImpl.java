package com.example.PortailRH.service;

import com.example.PortailRH.entity.*;
import com.example.PortailRH.entity.dto.NotificationRequest;
import com.example.PortailRH.entity.dto.TacheDTO;
import com.example.PortailRH.entity.enummerations.StatusTache;
import com.example.PortailRH.repository.ProjectRepo;
import com.example.PortailRH.repository.TacheRepo;
import com.example.PortailRH.repository.TeamRepo;
import com.example.PortailRH.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TacheServiceImpl implements TacheService{
    private final TacheRepo tacheRepo;
    private final UserRepo userRepo;
    private final TeamRepo teamRepository;
    private final JavaMailSender mailSender;
    private final ProjectRepo projectRepo;
    private final NotificationClient notificationClient;
    public TacheServiceImpl(TacheRepo tacheRepo, UserRepo userRepo, TeamRepo teamRepository, JavaMailSender mailSender, ProjectRepo projectRepo, NotificationClient notificationClient) {
        this.tacheRepo = tacheRepo;
        this.userRepo = userRepo;
        this.teamRepository = teamRepository;
        this.mailSender = mailSender;
        this.projectRepo = projectRepo;
        this.notificationClient = notificationClient;
    }


    @Override
    public Tache addTacheByChef(Long chefId, TacheDTO tacheDTO) {
        User chef = userRepo.findById(Math.toIntExact(chefId))
                .orElseThrow(() -> new RuntimeException("Chef not found"));

        Tache tache = new Tache();
        tache.setTitle(tacheDTO.getTitle());
        tache.setDescription(tacheDTO.getDescription());
        tache.setDateDebut(tacheDTO.getDateDebut());
        tache.setDateFin(tacheDTO.getDateFin());
        tache.setStatusTache(tacheDTO.getStatusTache() != null ? tacheDTO.getStatusTache() : StatusTache.NOT_ASSIGNED);
        tache.setComplexite(tacheDTO.getComplexite());
        tache.setCreatedBy(chef);
        Projet projet = projectRepo.findById(tacheDTO.getProjectId()).orElseThrow(() -> new RuntimeException("Project not found"));
        tache.setProjet(projet);
        return tacheRepo.save(tache);
    }


    @Override
    public List<Tache> getallTache() {
        return tacheRepo.findAll();
    }

    @Override
    public Optional<Tache> getTacheById(Long id) {
        return tacheRepo.findById(id);
    }
    @Override
    public Tache updateStatus(Long tacheId, StatusTache newStatus) {
        Tache tache = tacheRepo.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID: " + tacheId));
        tache.setStatusTache(newStatus);
        return tacheRepo.save(tache);
    }

    @Override
    public Tache assignTacheToCollaborator(Long tacheId, Long chefId, Long collaboratorId) {
        User chef = userRepo.findById(Math.toIntExact(chefId))
                .orElseThrow(() -> new RuntimeException("Chef not found"));
        User collaborator = userRepo.findById(Math.toIntExact(collaboratorId))
                .orElseThrow(() -> new RuntimeException("Collaborator not found"));
        Tache tache = tacheRepo.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tache not found"));
        // Récupérer team manager
        List<Team> teams = teamRepository.findByChef(chef);
        if (teams.isEmpty()) {
            throw new RuntimeException("Chef does not manage any team");
        }
        Team team = teams.get(0);

        if (!team.getCollaborators().contains(collaborator)) {
            throw new RuntimeException("The collaborator does not belong to the same team as the chef");
        }
        tache.setUser(collaborator);
        tache.setStatusTache(StatusTache.ASSIGNED);
        Tache tacheSaved = tacheRepo.save(tache);
       // sendEmailToCollaborator(collaborator, tache);

        NotificationRequest req = NotificationRequest.builder()
                .type("info")
                .message("Une nouvelle tâche vous a été assignée")
                .build();
        notificationClient.sendNotification(collaboratorId, req);

        return tacheSaved;
    }



    private void sendEmailToCollaborator(User collaborator, Tache tache) {
        try {
            String subject = "Nouvelle tâche assignée : " + tache.getTitle();
            String message = String.format("Bonjour %s,\n\n" +
                            "Vous avez été assigné à une nouvelle tâche : %s.\n" +
                            "Description : %s\n" +
                            "Merci de vérifier vos tâches pour plus de détails.\n\n" +
                            "Cordialement,\nVotre équipe",
                    collaborator.getUsername(), tache.getTitle(), tache.getDescription());
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(collaborator.getEmail());
            email.setSubject(subject);
            email.setText(message);
            mailSender.send(email);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email to collaborator", e);
        }
    }
    @Override
    public List<Tache> gettacheByChef(Long chefId) {
        User chef = userRepo.findById(Math.toIntExact(chefId))
                .orElseThrow(() -> new RuntimeException("Chef not found"));

        return tacheRepo.findAllByCreatedBy(chef);
    }

    @Override
    public List<Tache> getAssignedTachesByChef(Long chefId, Long collaboratorId) {
        User chef = userRepo.findById(Math.toIntExact(chefId))
                .orElseThrow(() -> new RuntimeException("Chef not found"));

        User collaborator = userRepo.findById(Math.toIntExact(collaboratorId))
                .orElseThrow(() -> new RuntimeException("Collaborator not found"));

        List<Team> teams = teamRepository.findByChef(chef);
        if (teams.isEmpty()) {
            throw new RuntimeException("Chef does not manage any team");
        }
        Team team = teams.get(0);
        if (!team.getCollaborators().contains(collaborator)) {
            throw new RuntimeException("The collaborator does not belong to the same team as the chef");
        }
        return tacheRepo.findAllByCreatedByAndUserAndStatusTache(chef, collaborator, StatusTache.ASSIGNED);
    }

    @Override
    public String getAssignedCollaboratorUsername(Long tacheId) {
        Tache tache = tacheRepo.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tache not found"));
        User assignedUser = tache.getUser();
        if (assignedUser == null) {
            throw new RuntimeException("No collaborator assigned to this task");
        }

        return assignedUser.getUsername();
    }
@Override
public Map<StatusTache, Long> countTachesByStatusAndChef(Long chefId) {
    User chef = userRepo.findById(Math.toIntExact(chefId))
            .orElseThrow(() -> new RuntimeException("Chef not found"));

    List<Tache> taches = tacheRepo.findAllByCreatedBy(chef);

    return taches.stream()
            .collect(Collectors.groupingBy(Tache::getStatusTache, Collectors.counting()));
}
    @Override
    public Map<String, Long> countTachesAssignedByChefToCollaborators(Long chefId) {
        User chef = userRepo.findById(Math.toIntExact(chefId))
                .orElseThrow(() -> new RuntimeException("Chef non trouvé avec l'ID : " + chefId));

        List<Tache> taches = tacheRepo.findAllByCreatedBy(chef);

        return taches.stream()
                .filter(tache -> tache.getUser() != null)
                .collect(Collectors.groupingBy(
                        tache -> tache.getUser().getUsername(),
                        Collectors.counting()
                ));
    }

    @Override
    public List<Tache> getTachesByCollaboratorId(Long collaboratorId) {
        User collaborator = userRepo.findById(Math.toIntExact(collaboratorId))
                .orElseThrow(() -> new RuntimeException("Collaborator not found"));

        return tacheRepo.findAllByUser(collaborator);
    }

    @Override
    public void DeleteTache(Long id) {
        tacheRepo.deleteById(id);
    }

}




