package com.example.PortailRH.service;

import com.example.PortailRH.entity.User;
import com.example.PortailRH.repository.UserRepo;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.sf.jasperreports.view.JasperViewer;

import java.io.File;
import java.io.InputStream;
import java.util.*;

@Service
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final UserRepo userRepo;

    @Value("${reports.output.directory:./reports/output/}")
    private String outputDirectory;

    @Value("${reports.directory:classpath:/reports/}")
    private String reportsDirectory;

    public ReportService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Charge un sous-rapport à partir du chemin spécifié
     */
    private JasperReport loadSubreport(String subReportPath) throws Exception {
        logger.info("Chargement du sous-rapport : {}", subReportPath);
        Resource reportResource = new ClassPathResource(subReportPath);
        try (InputStream inputStream = reportResource.getInputStream()) {
            if (inputStream == null) {
                throw new RuntimeException("Sous-rapport non trouvé: " + subReportPath);
            }
            return JasperCompileManager.compileReport(inputStream);
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du sous-rapport {}: {}", subReportPath, e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public JasperPrint generateUserReport(User user) {
        try {
            // S'assurer que les collections de l'utilisateur sont initialisées
            if (user != null) {
                logger.info("Génération du rapport pour l'utilisateur: {} (ID: {})", user.getUsername(), user.getId());
            }

            // Chemin du rapport principal
            String mainReportPath = "/reports/user_report_extended.jrxml";
            logger.info("Chargement du fichier de rapport : {}", mainReportPath);

            Resource reportResource = new ClassPathResource(mainReportPath);
            InputStream reportStream = reportResource.getInputStream();

            if (reportStream == null) {
                logger.error("Le fichier de rapport n'a pas été trouvé : {}", mainReportPath);
                throw new RuntimeException("Le fichier de rapport n'a pas été trouvé : " + mainReportPath);
            }

            logger.info("Compilation du rapport principal");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            logger.info("Configuration des paramètres du rapport");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);

            // Précompiler les sous-rapports
            JasperReport certificatsSubreport = loadSubreport("/reports/subreports/certificats_subreport.jrxml");
            JasperReport competencesSubreport = loadSubreport("/reports/subreports/competences_subreport.jrxml");
            JasperReport demandesSubreport = loadSubreport("/reports/subreports/demandes_subreport.jrxml");
            JasperReport formationsSubreport = loadSubreport("/reports/subreports/formations_subreport.jrxml");

            // Ajouter les sous-rapports compilés comme paramètres
            parameters.put("certificats_subreport", certificatsSubreport);
            parameters.put("competences_subreport", competencesSubreport);
            parameters.put("demandes_subreport", demandesSubreport);
            parameters.put("formations_subreport", formationsSubreport);

            // Log des détails importants
            if (user != null && user.getFormations() != null) {
                logger.info("Nombre de formations: {}", user.getFormations().size());
                for (int i = 0; i < user.getFormations().size(); i++) {
                    logger.info("Formation {}: {}", i, user.getFormations().get(i).getNom());

                    if (user.getFormations().get(i).getCompetances() != null) {
                        logger.info("   - Nombre de compétences: {}", user.getFormations().get(i).getCompetances().size());
                    } else {
                        logger.info("   - Compétences: null");
                    }

                    if (user.getFormations().get(i).getCertificats() != null) {
                        logger.info("   - Nombre de certificats: {}", user.getFormations().get(i).getCertificats().size());
                    } else {
                        logger.info("   - Certificats: null");
                    }
                }
            }

            logger.info("Création de la source de données");
            JRDataSource dataSource = new JRBeanCollectionDataSource(
                    Optional.ofNullable(user).map(Collections::singletonList).orElse(Collections.emptyList())
            );

            logger.info("Remplissage du rapport");
            return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        } catch (JRException e) {
            logger.error("Erreur lors de la compilation ou du remplissage du rapport", e);
            throw new RuntimeException("Erreur de génération du rapport: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la génération du rapport", e);
            throw new RuntimeException("Erreur de génération du rapport: " + e.getMessage(), e);
        }
    }

    public void generateUserReportById(Integer userId) {
        logger.info("Génération du rapport pour l'utilisateur ID: {}", userId);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé avec l'ID: {}", userId);
                    return new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId);
                });
        JasperPrint jasperPrint = generateUserReport(user);

        logger.info("Affichage du rapport dans la visionneuse JasperViewer");
        JasperViewer.viewReport(jasperPrint, false);
    }

    public String exportUserReportToPdf(Integer userId) {
        try {
            logger.info("Exportation du rapport en PDF pour l'utilisateur ID: {}", userId);
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> {
                        logger.error("Utilisateur non trouvé avec l'ID: {}", userId);
                        return new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId);
                    });

            JasperPrint jasperPrint = generateUserReport(user);

            File outputDir = new File(outputDirectory);
            if (!outputDir.exists()) {
                logger.info("Création du répertoire de sortie: {}", outputDirectory);
                outputDir.mkdirs();
            }

            String outputFile = outputDirectory + "user_" + userId + "_report_" + System.currentTimeMillis() + ".pdf";
            logger.info("Exportation du rapport vers: {}", outputFile);

            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);
            return outputFile;
        } catch (Exception e) {
            logger.error("Erreur lors de l'exportation du rapport en PDF", e);
            throw new RuntimeException("Erreur lors de l'exportation du rapport en PDF: " + e.getMessage(), e);
        }
    }

    public byte[] generateUserReportAsPdf(Integer userId) {
        logger.info("Génération du rapport PDF en mémoire pour l'utilisateur ID: {}", userId);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé avec l'ID: {}", userId);
                    return new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId);
                });
        JasperPrint jasperPrint = generateUserReport(user);

        try {
            logger.info("Conversion du rapport en bytes PDF");
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            logger.error("Erreur lors de la génération du PDF en mémoire", e);
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
}