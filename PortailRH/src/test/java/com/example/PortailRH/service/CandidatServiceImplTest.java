package com.example.PortailRH.service;

import com.example.PortailRH.entity.Candidat;
import com.example.PortailRH.entity.Offers;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.CandidarDTO;
import com.example.PortailRH.repository.CandidatRepo;
import com.example.PortailRH.repository.OffersRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CandidatServiceImplTest {
    @Mock
    private OffersRepo offersRepo;

    @Mock
    private CandidatRepo candidatRepo;

    @Mock
    private EmailService emailService;
    @InjectMocks
    private CandidatServiceImpl service;

    @Captor
    private ArgumentCaptor<Candidat> candidatCaptor;

    private CandidarDTO dto;
    private Offers mockOffer;
    private User mockUser;

    @BeforeEach
    void setUp() {
        dto = new CandidarDTO();
        dto.setOffersId(1L);
        dto.setNom("ala");
        dto.setPrenom("Hatem");
        dto.setEmail("hatem@gmail.com");
        dto.setTelephone("23456789");
        dto.setCv("cv.pdf");

        mockUser = new User();
        mockUser.setUsername("recruiter");
        mockUser.setEmail("test@gmail.com");

        mockOffer = new Offers();
        mockOffer.setId(1L);
        mockOffer.setTitle("Developer");
        mockOffer.setUser(mockUser);
    }

    @Test
    void postuler_ShouldSaveCandidatAndNotSendEmail_WhenUserPresent_ButEmailDisabled() {
        when(offersRepo.findById(1L)).thenReturn(Optional.of(mockOffer));
        when(candidatRepo.save(any(Candidat.class))).thenAnswer(i -> i.getArgument(0));

        Candidat result = service.postuler(dto);

        verify(candidatRepo).save(candidatCaptor.capture());
        Candidat saved = candidatCaptor.getValue();

        assertThat(saved.getNom()).isEqualTo(dto.getNom());
        assertThat(saved.getPrenom()).isEqualTo(dto.getPrenom());
        assertThat(saved.getEmail()).isEqualTo(dto.getEmail());
        assertThat(saved.getTelephone()).isEqualTo(dto.getTelephone());
        assertThat(saved.getCv()).isEqualTo(dto.getCv());
        assertThat(saved.getOffers()).isEqualTo(mockOffer);

        assertThat(result).isEqualTo(saved);
        // Email is commented out in service, so ensure no email sent
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void postuler_ShouldThrowException_WhenOfferNotFound() {
        when(offersRepo.findById(2L)).thenReturn(Optional.empty());
        dto.setOffersId(2L);

        assertThrows(RuntimeException.class, () -> service.postuler(dto));
        verify(candidatRepo, never()).save(any());
    }

    @Test
    void getAllCandidatures_ShouldReturnAllFromRepo() {
        Candidat c1 = new Candidat();
        Candidat c2 = new Candidat();
        when(candidatRepo.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<Candidat> list = service.getAllCandidatures();

        assertThat(list).containsExactly(c1, c2);
    }

    @Test
    void getAllCandidateEmails_ShouldReturnEmails() {
        Candidat c1 = new Candidat(); c1.setEmail("a@x.com");
        Candidat c2 = new Candidat(); c2.setEmail("b@y.com");
        when(candidatRepo.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<String> emails = service.getAllCandidateEmails();
        assertThat(emails).containsExactly("a@x.com", "b@y.com");
    }

    @Test
    void deltetCandidatById_ShouldCallDelete() {
        service.deltetCandidatById(5L);
        verify(candidatRepo).deleteById(5);
    }

    @Test
    void sendInterviewEmail_ShouldSendEmailWithCorrectContent() {
        Candidat candidat = new Candidat();
        candidat.setId(10L);
        candidat.setNom("tt");
        candidat.setEmail("tt@gmail.com");
        candidat.setTelephone("23568974");
        candidat.setCv("jane.pdf");
        candidat.setOffers(mockOffer);

        when(candidatRepo.findById(10)).thenReturn(Optional.of(candidat));

        service.sendInterviewEmail(10L, "2025-06-01", "10:00", "https://meeting.link");

        verify(emailService).sendEmail(eq("tt@gmail.com"), eq("Invitation à un entretien pour Developer"), anyString());
    }
}
