package com.example.PortailRH.service;

import com.example.PortailRH.entity.Certificat;
import com.example.PortailRH.entity.dto.CertifDTO;

import java.util.List;
import java.util.Optional;

public interface CertificatService {
    public Certificat addCertif(CertifDTO certifDTO);
    public List<Certificat> getallCertif();
    public Optional<Certificat> getCertifByID(Long id);
}
