package com.example.PortailRH.service;

import com.example.PortailRH.entity.Certificat;
import com.example.PortailRH.entity.Formation;
import com.example.PortailRH.entity.dto.CertifDTO;
import com.example.PortailRH.repository.CertifRepo;
import com.example.PortailRH.repository.FormationRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CertificatServiceImpl implements CertificatService{
    private final CertifRepo certifRepo;
    private final FormationRepo formationRepo;

    public CertificatServiceImpl(CertifRepo certifRepo, FormationRepo formationRepo) {
        this.certifRepo = certifRepo;
        this.formationRepo = formationRepo;
    }
    @Override
    public Certificat addCertif(CertifDTO certifDTO) {
        Certificat certificat = new Certificat();
        certificat.setNom(certifDTO.getNom());
        certificat.setDescription(certifDTO.getDescription());
        certificat.setUrl(certifDTO.getUrl());
        certificat.setDateExpiration(certifDTO.getDateExpiration());
        Formation formation = formationRepo.findById(certifDTO.getFormationId()).get();
        certificat.setFormation(formation);
        return certifRepo.save(certificat);
    }

    @Override
    public List<Certificat> getallCertif() {
        return certifRepo.findAll();
    }

    @Override
    public Optional<Certificat> getCertifByID(Long id) {
        return certifRepo.findById(id);
    }
}
