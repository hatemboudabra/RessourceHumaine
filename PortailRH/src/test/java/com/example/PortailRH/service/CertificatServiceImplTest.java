package com.example.PortailRH.service;

import com.example.PortailRH.entity.Certificat;
import com.example.PortailRH.entity.Formation;
import com.example.PortailRH.entity.dto.CertifDTO;
import com.example.PortailRH.repository.CertifRepo;
import com.example.PortailRH.repository.FormationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CertificatServiceImplTest {
    @Mock
    private CertifRepo certifRepo;

    @Mock
    private FormationRepo formationRepo;

    @InjectMocks
    private CertificatServiceImpl certificatService;

    // We don't need this with @ExtendWith(MockitoExtension.class)
    // @BeforeEach
    // void setUp() {
    //     MockitoAnnotations.openMocks(this);
    // }

    @Test
    void testAddCertif() {
        Long formationId = 1L;
        Formation formation = new Formation();
        formation.setId(formationId);

        CertifDTO certifDTO = new CertifDTO();
        certifDTO.setNom("Java Certification");
        certifDTO.setDescription("Oracle Java Certification");
        certifDTO.setUrl("https://java.com/cert");
        certifDTO.setDateExpiration(LocalDate.now().plusYears(2));
        certifDTO.setFormationId(formationId);

        Certificat expectedCertificat = new Certificat();
        expectedCertificat.setId(1L);
        expectedCertificat.setNom(certifDTO.getNom());
        expectedCertificat.setDescription(certifDTO.getDescription());
        expectedCertificat.setUrl(certifDTO.getUrl());
        expectedCertificat.setDateExpiration(certifDTO.getDateExpiration());
        expectedCertificat.setFormation(formation);

        when(formationRepo.findById(formationId)).thenReturn(Optional.of(formation));
        when(certifRepo.save(any(Certificat.class))).thenReturn(expectedCertificat);

        Certificat result = certificatService.addCertif(certifDTO);

        assertNotNull(result);
        assertEquals(expectedCertificat.getId(), result.getId());
        assertEquals(certifDTO.getNom(), result.getNom());
        assertEquals(certifDTO.getDescription(), result.getDescription());
        assertEquals(certifDTO.getUrl(), result.getUrl());
        assertEquals(certifDTO.getDateExpiration(), result.getDateExpiration());
        assertEquals(formation, result.getFormation());

        verify(formationRepo, times(1)).findById(formationId);
        verify(certifRepo, times(1)).save(any(Certificat.class));
    }

    @Test
    void testGetAllCertif() {
        Certificat cert1 = new Certificat();
        cert1.setId(1L);
        cert1.setNom("Cert 1");

        Certificat cert2 = new Certificat();
        cert2.setId(2L);
        cert2.setNom("Cert 2");

        List<Certificat> expectedCertificats = Arrays.asList(cert1, cert2);

        when(certifRepo.findAll()).thenReturn(expectedCertificats);
        List<Certificat> result = certificatService.getallCertif();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(expectedCertificats));

        verify(certifRepo, times(1)).findAll();
    }

    @Test
    void testGetCertifById_WhenExists() {
        Long certifId = 1L;
        Certificat expectedCertificat = new Certificat();
        expectedCertificat.setId(certifId);
        expectedCertificat.setNom("Test Certificate");
        when(certifRepo.findById(certifId)).thenReturn(Optional.of(expectedCertificat));
        Optional<Certificat> result = certificatService.getCertifByID(certifId);
        assertTrue(result.isPresent(), "Certificate should be present");
        assertEquals(expectedCertificat.getId(), result.get().getId());
        assertEquals(expectedCertificat.getNom(), result.get().getNom());

        verify(certifRepo, times(1)).findById(certifId);
    }

    @Test
    void testGetCertifById_WhenNotExists() {
        Long certifId = 999L;

        when(certifRepo.findById(certifId)).thenReturn(Optional.empty());
        Optional<Certificat> result = certificatService.getCertifByID(certifId);

        assertFalse(result.isPresent(), "Certificate should not be present");

        verify(certifRepo).findById(certifId);
    }
}