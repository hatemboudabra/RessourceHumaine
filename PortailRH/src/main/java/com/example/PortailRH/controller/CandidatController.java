package com.example.PortailRH.controller;

import com.example.PortailRH.entity.Candidat;
import com.example.PortailRH.entity.dto.CandidarDTO;
import com.example.PortailRH.fileManager.FileFilter;
import com.example.PortailRH.service.CandidatService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class CandidatController {
    private final CandidatService candidatService;
    @Autowired
    private FileFilter fileFilter;
    public CandidatController(CandidatService candidatService) {
        this.candidatService = candidatService;
    }
    @PostMapping("postuler")
    public ResponseEntity<?> postuler(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("email") String email,
            @RequestParam("telephone") String telephone,
            @RequestParam("offersId") Long offersId) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Le fichier CV est requis.");
            }

            if (!file.getContentType().equals("application/pdf")) {
                return ResponseEntity.badRequest().body("Seuls les fichiers PDF sont acceptés.");
            }

            String fileName = fileFilter.storeFile(file);

            CandidarDTO candidarDTO = new CandidarDTO();
            candidarDTO.setNom(nom);
            candidarDTO.setPrenom(prenom);
            candidarDTO.setEmail(email);
            candidarDTO.setTelephone(telephone);
            candidarDTO.setCv(fileName);
            candidarDTO.setOffersId(offersId);

            Candidat candidat = candidatService.postuler(candidarDTO);

            return ResponseEntity.ok(candidat);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de la sauvegarde du fichier.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de la candidature.");
        }
    }

    @GetMapping("/cvs/{filename}")
    public ResponseEntity<byte[]> getCV(@PathVariable String filename) {
        try {
            String repertoireCV = "uploads/cvs";
            File cvFile = new File(repertoireCV, filename);

            if (cvFile.exists()) {
                byte[] cvBytes = Files.readAllBytes(cvFile.toPath());
                return ResponseEntity.ok()
                        .header("Content-Type", "application/pdf")
                        .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                        .body(cvBytes);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/allcandidat")
    public List<Candidat> getAllCandidatures() {
        return candidatService.getAllCandidatures();
    }
    @GetMapping("/emails")
    public List<String> getAllCandidateEmails() {
        return candidatService.getAllCandidateEmails();
    }
    @DeleteMapping("Candiat/{id}")
    public ResponseEntity<Void> deleteCandidat(@PathVariable Long id) {
       candidatService.deltetCandidatById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleInterview(
            @RequestParam Long candidatId,
            @RequestParam String interviewDate,
            @RequestParam String interviewTime,
            @RequestParam String meetingLink) {

        try {
            candidatService.sendInterviewEmail(candidatId, interviewDate, interviewTime, meetingLink);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Candidat non trouvé",
                            "message", e.getMessage(),
                            "candidatId", candidatId
                    ));
        }
    }
}
