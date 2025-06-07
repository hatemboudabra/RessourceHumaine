package com.example.PortailRH.service;

import com.example.PortailRH.entity.Demande;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.DemandeDTO;
import com.example.PortailRH.entity.dto.NotificationRequest;
import com.example.PortailRH.entity.enummerations.Status;
import com.example.PortailRH.entity.enummerations.Type;
import com.example.PortailRH.repository.DemandeRepo;
import com.example.PortailRH.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemandeServiceImplTest {

    @Mock
    private DemandeRepo demandeRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private DemandeServiceImpl demandeService;

    private DemandeDTO demandeDTO;
    private Demande demande;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setUsername("hatem");

        demandeDTO = new DemandeDTO();
        demandeDTO.setTitle("Test Demande");
        demandeDTO.setDescription("Test Description");
        demandeDTO.setDate(new Date());
        demandeDTO.setStatus(Status.PENDING);
        demandeDTO.setType(Type.DOCUMENT);
        demandeDTO.setUserId(1L);
        demandeDTO.setDocumentType("Test Document Type");

        demande = new Demande();
        demande.setId(1L);
        demande.setTitle("Test Demande");
        demande.setDescription("Test Description");
        demande.setDate(new Date());
        demande.setStatus(Status.PENDING);
        demande.setType(Type.DOCUMENT);
        demande.setUser(user);
        demande.setDocumentType("Test Document Type");
    }

    @Test
    void addDemande_Success() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(demandeRepo.save(any(Demande.class))).thenReturn(demande);

        // Act
        Demande result = demandeService.addDemande(demandeDTO);

        // Assert
        assertNotNull(result);
        assertEquals(demandeDTO.getTitle(), result.getTitle());
        assertEquals(demandeDTO.getDescription(), result.getDescription());
        assertEquals(demandeDTO.getType(), result.getType());
        assertEquals(user, result.getUser());

        verify(userRepo, times(1)).findById(1);
        verify(demandeRepo, times(1)).save(any(Demande.class));
    }

    @Test
    void addDemande_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            demandeService.addDemande(demandeDTO);
        });

        verify(userRepo, times(1)).findById(1);
        verify(demandeRepo, never()).save(any());
    }

    @Test
    void updateDemande_Success() {
        // Arrange
        when(demandeRepo.findById(1L)).thenReturn(Optional.of(demande));
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(demandeRepo.save(any(Demande.class))).thenReturn(demande);

        // Act
        Demande result = demandeService.UpdateDemande(1L, demandeDTO);

        // Assert
        assertNotNull(result);
        assertEquals(demandeDTO.getTitle(), result.getTitle());
        assertEquals(demandeDTO.getDescription(), result.getDescription());

        verify(demandeRepo, times(1)).findById(1L);
        verify(demandeRepo, times(1)).save(any(Demande.class));
    }

    @Test
    void updateDemande_NotFound_ThrowsException() {
        // Arrange
        when(demandeRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            demandeService.UpdateDemande(1L, demandeDTO);
        });

        verify(demandeRepo, times(1)).findById(1L);
        verify(demandeRepo, never()).save(any());
    }

    @Test
    void getAllDemandes_Success() {
        // Arrange
        List<Demande> expectedDemandes = Arrays.asList(demande);
        when(demandeRepo.findAll()).thenReturn(expectedDemandes);

        // Act
        List<Demande> result = demandeService.getallDemande();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(demandeRepo, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        // Arrange
        when(demandeRepo.findById(1L)).thenReturn(Optional.of(demande));

        // Act
        Optional<Demande> result = demandeService.getById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(demande.getId(), result.get().getId());
        verify(demandeRepo, times(1)).findById(1L);
    }

    @Test
    void changeStatus_Success() {
        // Arrange
        when(demandeRepo.findById(1L)).thenReturn(Optional.of(demande));
        when(demandeRepo.save(any(Demande.class))).thenReturn(demande);

        // Act
        Demande result = demandeService.changeStatus(1L, Status.APPROVED);

        // Assert
        assertNotNull(result);
        assertEquals(Status.APPROVED, result.getStatus());
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        verify(demandeRepo, times(1)).findById(1L);
        verify(demandeRepo, times(1)).save(any(Demande.class));
    }

    @Test
    void changeStatus_InvalidStatus_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            demandeService.changeStatus(1L, Status.PENDING);
        });

        verify(demandeRepo, never()).findById(any());
        verify(demandeRepo, never()).save(any());
    }

    @Test
    void changeStatusForLoanOrAdvance_Success() {
        // Arrange
        demande.setType(Type.LOAN);
        when(demandeRepo.findById(1L)).thenReturn(Optional.of(demande));
        when(demandeRepo.save(any(Demande.class))).thenReturn(demande);

        // Act
        Demande result = demandeService.changeStatusForLoanOrAdvance(1L, Status.APPROVED);

        // Assert
        assertNotNull(result);
        assertEquals(Status.APPROVED, result.getStatus());
        verify(notificationClient, times(1)).sendNotification(any(), any());
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void changeStatusForDocumentOrTrainingOrLeave_Success() {
        // Arrange
        demande.setType(Type.DOCUMENT);
        when(demandeRepo.findById(1L)).thenReturn(Optional.of(demande));
        when(demandeRepo.save(any(Demande.class))).thenReturn(demande);

        // Act
        Demande result = demandeService.changeStatusForDocumentOrTrainingOrLeave(1L, Status.APPROVED);

        // Assert
        assertNotNull(result);
        assertEquals(Status.APPROVED, result.getStatus());
        verify(notificationClient, times(1)).sendNotification(any(), any());
    }

    @Test
    void getLoanOrAdvanceRequests_Success() {
        // Arrange
        List<Demande> expected = Arrays.asList(demande);
        when(demandeRepo.findByTypeIn(Arrays.asList(Type.LOAN, Type.ADVANCE))).thenReturn(expected);

        // Act
        List<Demande> result = demandeService.getLoanOrAdvanceRequests();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getDocumentTrainingOrLeaveRequests_Success() {
        // Arrange
        List<Demande> expected = Arrays.asList(demande);
        when(demandeRepo.findByTypeIn(Arrays.asList(Type.DOCUMENT, Type.TRAINING, Type.LEAVE))).thenReturn(expected);

        // Act
        List<Demande> result = demandeService.getDocumentTrainingOrLeaveRequests();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getDemandesByUserId_Success() {
        // Arrange
        List<Demande> expected = Arrays.asList(demande);
        when(demandeRepo.findByUserId(1L)).thenReturn(expected);

        // Act
        List<Demande> result = demandeService.getDemandesByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getDemandesCountByStatusForDocumentTrainingOrLeave_Success() {
        // Arrange
        Map<Status, Long> expected = new HashMap<>();
        expected.put(Status.PENDING, 5L);
        expected.put(Status.APPROVED, 3L);

        when(demandeRepo.countDemandesByStatusForTypes(Arrays.asList(Type.DOCUMENT, Type.TRAINING, Type.LEAVE)))
                .thenReturn(Arrays.asList(
                        new Object[]{Status.PENDING, 5L},
                        new Object[]{Status.APPROVED, 3L}
                ));

        // Act
        Map<Status, Long> result = demandeService.getDemandesCountByStatusForDocumentTrainingOrLeave();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(5L, result.get(Status.PENDING));
    }

    @Test
    void deleteDemande_Success() {
        // Arrange
        doNothing().when(demandeRepo).deleteById(1L);

        // Act
        demandeService.DeleteDemande(1L);

        // Assert
        verify(demandeRepo, times(1)).deleteById(1L);
    }
}