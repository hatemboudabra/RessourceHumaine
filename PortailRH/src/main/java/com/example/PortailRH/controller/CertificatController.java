package com.example.PortailRH.controller;

import com.example.PortailRH.entity.Certificat;
import com.example.PortailRH.entity.dto.CertifDTO;
import com.example.PortailRH.service.CertificatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class CertificatController {
    private final CertificatService certificatService;

    public CertificatController(CertificatService certificatService) {
        this.certificatService = certificatService;
    }
    @PostMapping("/addCertif")
    public ResponseEntity<Certificat> addCertificat(@RequestBody CertifDTO certifDTO) {
        Certificat certificat = certificatService.addCertif(certifDTO);
        return ResponseEntity.ok(certificat);
    }
    @GetMapping("/allCertificat")
    public ResponseEntity<List<Certificat>> getAllCertificats() {
        List<Certificat> certificats = certificatService.getallCertif();
        return ResponseEntity.ok(certificats);
    }
    @GetMapping("Certificat/{id}")
    public ResponseEntity<Certificat> getCertificat(@PathVariable Long id) {
        Optional<Certificat> certificat = certificatService.getCertifByID(id);
        return certificat.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
