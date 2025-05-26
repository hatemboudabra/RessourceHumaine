package com.example.PortailRH.controller;

import com.example.PortailRH.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/preview/{userId}")
    public ResponseEntity<String> previewReport(@PathVariable Integer userId) {
        try {
            reportService.generateUserReportById(userId);
            return ResponseEntity.ok("Rapport généré avec succès");
        } catch (Exception e) {
            logger.error("Erreur de prévisualisation", e);
            return ResponseEntity.internalServerError().body("Erreur de prévisualisation");
        }
    }

    @GetMapping("/download/{userId}")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Integer userId) {
        try {
            byte[] pdfBytes = reportService.generateUserReportAsPdf(userId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "user_report_" + userId + ".pdf");
            headers.setCacheControl("must-revalidate");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            logger.error("Erreur de téléchargement", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/generate/{userId}")
    public ResponseEntity<String> generateReport(@PathVariable Integer userId) {
        try {
            String filePath = reportService.exportUserReportToPdf(userId);
            return ResponseEntity.ok("Rapport généré: " + filePath);
        } catch (Exception e) {
            logger.error("Erreur de génération", e);
            return ResponseEntity.internalServerError().body("Erreur de génération");
        }
    }
}