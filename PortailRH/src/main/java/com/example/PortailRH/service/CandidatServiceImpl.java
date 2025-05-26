package com.example.PortailRH.service;

import com.example.PortailRH.entity.Candidat;
import com.example.PortailRH.entity.Offers;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.CandidarDTO;
import com.example.PortailRH.repository.CandidatRepo;
import com.example.PortailRH.repository.OffersRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidatServiceImpl implements CandidatService {
    private final OffersRepo offersRepo;
    private  final CandidatRepo candidatRepo;
    private final EmailService emailService;
    private final List<Candidat> candidatures = new ArrayList<>();
    public CandidatServiceImpl(OffersRepo offersRepo, CandidatRepo candidatRepo, EmailService emailService) {
        this.offersRepo = offersRepo;
        this.candidatRepo = candidatRepo;
        this.emailService = emailService;
    }

    @Override
    public Candidat postuler(CandidarDTO candidarDTO) {
        Offers offer = offersRepo.findById(candidarDTO.getOffersId())
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        Candidat candidat = new Candidat();
        candidat.setNom(candidarDTO.getNom());
        candidat.setPrenom(candidarDTO.getPrenom());
        candidat.setEmail(candidarDTO.getEmail());
        candidat.setTelephone(candidarDTO.getTelephone());
        candidat.setCv(candidarDTO.getCv());
        candidat.setOffers(offer);

        Candidat savedCandidat = candidatRepo.save(candidat);

        User user = offer.getUser();
        if (user != null) {
            String subject = "Nouvelle candidature pour votre offre : " + offer.getTitle();
            String downloadLink = "http://localhost:8082/cvs/" + candidat.getCv(); // Lien de téléchargement du CV
            String text = "Bonjour " + user.getUsername() + ",\n\n"
                    + "Un nouveau candidat a postulé à votre offre : " + offer.getTitle() + ".\n"
                    + "Détails du candidat :\n"
                    + "- Nom : " + candidat.getNom() + "\n"
                    + "- Prénom : " + candidat.getPrenom() + "\n"
                    + "- Email : " + candidat.getEmail() + "\n"
                    + "- Téléphone : " + candidat.getTelephone() + "\n"
                    + "- CV : " + downloadLink + "\n\n"
                    + "Cordialement,\n"
                    + "Votre équipe RH";

       //     emailService.sendEmail(user.getEmail(), subject, text);
        }

        return savedCandidat;
    }

    @Override
    public List<Candidat> getAllCandidatures() {
        return candidatRepo.findAll();
    }
    @Override
    public List<String> getAllCandidateEmails() {
        return candidatRepo.findAll().stream()
                .map(Candidat::getEmail)
                .collect(Collectors.toList());
    }

    @Override
    public void deltetCandidatById(Long id) {
        candidatRepo.deleteById(Math.toIntExact(id));
    }

    @Override
    public void sendInterviewEmail(Long candidatId, String interviewDate, String interviewTime, String meetingLink) {
        Candidat candidat = candidatRepo.findById(Math.toIntExact(candidatId))
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé"));

        Offers offer = candidat.getOffers();
        String offerTitle = offer != null ? offer.getTitle() : "une offre chez nous";

        String subject = "Invitation à un entretien pour " + offerTitle;
        String text = "Bonjour " + candidat.getNom() + ",\n\n"
                + "Nous sommes heureux de vous informer que votre candidature pour le poste \""
                + offerTitle + "\" a retenu notre attention.\n"
                + "Nous vous invitons à un entretien qui se déroulera :\n"
                + "- Date : " + interviewDate + "\n"
                + "- Heure : " + interviewTime + "\n"
                + "- Lien de la réunion : " + meetingLink + "\n\n"
                + "Merci de confirmer votre présence en répondant à cet email.\n\n"
                + "Cordialement,\n"
                + "L'équipe des Ressources Humaines";

        emailService.sendEmail(candidat.getEmail(), subject, text);
    }


}
