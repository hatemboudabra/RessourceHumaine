package com.example.PortailRH.service;

import com.example.PortailRH.entity.Demande;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.DemandeDTO;
import com.example.PortailRH.entity.dto.NotificationRequest;
import com.example.PortailRH.entity.enummerations.RoleName;
import com.example.PortailRH.entity.enummerations.Status;
import com.example.PortailRH.entity.enummerations.Type;
import com.example.PortailRH.repository.DemandeRepo;
import com.example.PortailRH.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DemandeServiceImpl implements DemandeSerice{

    private final DemandeRepo demandeRepo;
    private final UserRepo userRepo;
    private final EmailService emailService;
    private final NotificationClient notificationClient;
    public DemandeServiceImpl(DemandeRepo demandeRepo, UserRepo userRepo, EmailService emailService, NotificationClient notificationClient) {
        this.demandeRepo = demandeRepo;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.notificationClient = notificationClient;
    }

    @Override
    public Demande addDemande(DemandeDTO demandeDTO) {
        Demande demande = new Demande();
        demande.setTitle(demandeDTO.getTitle());
        demande.setDescription(demandeDTO.getDescription());
        demande.setDate(demandeDTO.getDate());
        demande.setStatus(demandeDTO.getStatus());
        demande.setType(demandeDTO.getType());
        User user = userRepo.findById(Math.toIntExact(demandeDTO.getUserId()))
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + demandeDTO.getUserId()));
        demande.setUser(user);
        switch (demandeDTO.getType()) {
            case DOCUMENT:
                demande.setDocumentType(demandeDTO.getDocumentType());
                break;
            case TRAINING:
                break;
            case LEAVE:
                demande.setNbrejour(demandeDTO.getNbrejour());
                break;
            case LOAN:
                demande.setAmount(demandeDTO.getAmount());
                demande.setLoanType(demandeDTO.getLoanType());
                break;
            case ADVANCE:
                demande.setAmount(demandeDTO.getAmount());
                break;
            default:
                throw new IllegalArgumentException("Type de demande non supporté : " + demandeDTO.getType());
        }

        return demandeRepo.save(demande);
    }

    @Override
    public Demande UpdateDemande(Long id, DemandeDTO demandeDTO) {
        Optional<Demande> optionalDemande = demandeRepo.findById(id);
        if(optionalDemande.isPresent()){
            Demande demande = optionalDemande.get();
            demande.setTitle(demandeDTO.getTitle());
            demande.setDescription(demandeDTO.getDescription());
            demande.setDate(demandeDTO.getDate());
            demande.setStatus(demandeDTO.getStatus());
            demande.setType(demandeDTO.getType());
            User user = userRepo.findById(Math.toIntExact(demandeDTO.getUserId())).get();
            if (user != null) {
                demande.setUser(user);
            }
            demandeRepo.save(demande);
            return demande;

        }else {
            throw new EntityNotFoundException("Demande with id " + id + " not found");

        }


    }

    @Override
    public List<Demande> getallDemande() {
        return demandeRepo.findAll();
    }

    @Override
    public Optional<Demande> getById(Long id) {
        return demandeRepo.findById(id);
    }

    @Override
    public Demande changeStatus(Long id, Status newStatus) {
        if (newStatus != Status.APPROVED && newStatus != Status.REJECTED) {
            throw new IllegalArgumentException("Le statut doit être APPROVED ou REJECTED");
        }
        Optional<Demande> optionalDemande = demandeRepo.findById(id);
        if (optionalDemande.isPresent()) {
            Demande demande = optionalDemande.get();
            if (demande.getStatus() != Status.PENDING) {
                throw new IllegalStateException("Le statut ne peut être modifié que si l'état actuel est PENDING");
            }
            demande.setStatus(newStatus);
            Demande updatedDemande = demandeRepo.save(demande);

            User user = demande.getUser();
            String email = user.getEmail();
            String subject = "Mise à jour du statut de votre demande";
            String body = String.format(
                    "Bonjour %s,\n\nLe statut de votre demande \"%s\" a été mis à jour.\n\nNouveau statut : %s\n\nCordialement,\nL'équipe",
                    user.getUsername(),
                    demande.getTitle(),
                    newStatus
            );
            emailService.sendEmail(email, subject, body);

            return updatedDemande;
        } else {
            throw new EntityNotFoundException("La demande avec l'ID " + id + " est introuvable");
        }
    }


    @Override
    public Demande changeStatusForLoanOrAdvance(Long id, Status newStatus) {
        if (newStatus != Status.APPROVED && newStatus != Status.REJECTED) {
            throw new IllegalArgumentException("Le statut doit être APPROVED ou REJECTED");
        }
        Optional<Demande> optionalDemande = demandeRepo.findById(id);
        if (optionalDemande.isPresent()) {
            Demande demande = optionalDemande.get();
            if (demande.getType() != Type.LOAN && demande.getType() != Type.ADVANCE) {
                throw new IllegalStateException("Cette méthode est réservée aux types LOAN ou ADVANCE");
            }
            if (demande.getStatus() != Status.PENDING) {
                throw new IllegalStateException("Le statut ne peut être modifié que si l'état actuel est PENDING");
            }
            demande.setStatus(newStatus);
            Demande updatedDemande = demandeRepo.save(demande);
            User user = demande.getUser();
            String email = user.getEmail();
            String subject = "Mise à jour du statut de votre demande";
            String body = String.format(
                    "Bonjour %s,\n\nLe statut de votre demande \"%s\" a été mis à jour.\n\nNouveau statut : %s\n\nCordialement,\nL'équipe RH",
                    user.getUsername(),
                    demande.getTitle(),
                    newStatus
            );
            emailService.sendEmail(email, subject, body);
            //notif real time
            NotificationRequest req = NotificationRequest.builder()
                    .type("info")
                    .message(String.format("Votre demande \"%s\" a été %s.",
                            updatedDemande.getTitle(),
                            newStatus == Status.APPROVED ? "approuvée" : "rejetée"))
                    .build();
            notificationClient.sendNotification(user.getId(), req);

            return updatedDemande;
        } else {
            throw new EntityNotFoundException("La demande avec l'ID " + id + " est introuvable");
        }
    }


    @Override
    public Demande changeStatusForDocumentOrTrainingOrLeave(Long id, Status newStatus) {
        if (newStatus != Status.APPROVED && newStatus != Status.REJECTED) {
            throw new IllegalArgumentException("Le statut doit être APPROVED ou REJECTED");
        }
        Optional<Demande> optionalDemande = demandeRepo.findById(id);
        if (optionalDemande.isPresent()) {
            Demande demande = optionalDemande.get();
            if (demande.getType() != Type.DOCUMENT && demande.getType() != Type.TRAINING && demande.getType() != Type.LEAVE) {
                throw new IllegalStateException("Cette méthode est réservée aux types DOCUMENT, TRAINING, ou LEAVE");
            }
            if (demande.getStatus() != Status.PENDING) {
                throw new IllegalStateException("Le statut ne peut être modifié que si l'état actuel est PENDING");
            }
            demande.setStatus(newStatus);
            Demande updatedDemande = demandeRepo.save(demande);
            User user = demande.getUser();

            String body = String.format(
                    "Le statut de votre demande \"%s\" a été mis à jour. Nouveau statut : %s",
                    demande.getTitle(),
                    newStatus
            );
            // reel time notif
            NotificationRequest req = NotificationRequest.builder()
                    .type("info")
                    .message(String.format("Votre demande \"%s\" a été %s.",
                            updatedDemande.getTitle(),
                            newStatus == Status.APPROVED ? "approuvée" : "rejetée"))
                    .build();
            notificationClient.sendNotification(user.getId(), req);

            return updatedDemande;
        } else {
            throw new EntityNotFoundException("La demande avec l'ID " + id + " est introuvable");
        }
    }

    @Override
    public List<Demande> getLoanOrAdvanceRequests() {
        return demandeRepo.findByTypeIn(Arrays.asList(Type.LOAN, Type.ADVANCE));

    }

    @Override
    public List<Demande> getDocumentTrainingOrLeaveRequests() {
        return demandeRepo.findByTypeIn(Arrays.asList(Type.DOCUMENT, Type.TRAINING, Type.LEAVE));
    }

    @Override
    public List<Demande> getDemandesByUserId(Long userId) {
        return demandeRepo.findByUserId(userId);
    }

    @Override
    public Map<Status, Long> getDemandesCountByStatusForDocumentTrainingOrLeave() {
        List<Object[]> results = demandeRepo.countDemandesByStatusForTypes(
                Arrays.asList(Type.DOCUMENT, Type.TRAINING, Type.LEAVE)
        );
        Map<Status, Long> demandeCounts = new HashMap<>();
        for (Object[] result : results) {
            Status status = (Status) result[0];
            Long count = (Long) result[1];
            demandeCounts.put(status, count);
        }

        return demandeCounts;
    }

    @Override
    public Map<Status, Long> getDemandesCountByStatusForLoanOrAdvanceRequest() {
        List<Object[]> resuls = demandeRepo.countDemandesByStatusForTypes(
                Arrays.asList(Type.LOAN, Type.ADVANCE)
        );
        Map<Status, Long> demandeCount = new HashMap<>();
        for (Object[] result : resuls) {
            Status status = (Status) result[0];
            Long count = (Long) result[1];
            demandeCount.put(status, count);
        }
            return demandeCount;
    }

    @Override
    public void DeleteDemande(Long id) {
        demandeRepo.deleteById(id);
    }

    @Override
    public Map<String, Long> getDemandesCountByMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return demandeRepo.findAll().stream()
                .filter(demande -> demande.getStatus() != null)
                .collect(Collectors.groupingBy(
                        demande -> sdf.format(demande.getDate()),
                        Collectors.counting()
                ));
    }
}
