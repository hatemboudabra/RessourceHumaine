package com.example.PortailRH.service;

import com.example.PortailRH.entity.Formation;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.FormationDTO;
import com.example.PortailRH.repository.FormationRepo;
import com.example.PortailRH.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormationServiceImplTest {

    @Mock
    private FormationRepo formationRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private FormationServiceImpl formationService;

    private FormationDTO formationDTO;
    private Formation formation;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        formationDTO = new FormationDTO();
        formationDTO.setNom("Java Programming");
        formationDTO.setDescription("Learn Java fundamentals");
        formationDTO.setUserId(1L);

        formation = new Formation();
        formation.setId(1L);
        formation.setNom("Java Programming");
        formation.setDescription("Learn Java fundamentals");
        formation.setUser(user);
    }

    @Test
    void addFormation_Success() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(formationRepo.save(any(Formation.class))).thenReturn(formation);

        // Act
        Formation result = formationService.addFormation(formationDTO);

        // Assert
        assertNotNull(result);
        assertEquals(formationDTO.getNom(), result.getNom());
        assertEquals(formationDTO.getDescription(), result.getDescription());
        assertEquals(user, result.getUser());

        verify(userRepo, times(1)).findById(1);
        verify(formationRepo, times(1)).save(any(Formation.class));
    }

    @Test
    void addFormation_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            formationService.addFormation(formationDTO);
        });

        verify(userRepo, times(1)).findById(1);
        verify(formationRepo, never()).save(any());
    }

    @Test
    void updateFormation_Success() {
        // Arrange
        FormationDTO updatedDTO = new FormationDTO();
        updatedDTO.setNom("Advanced Java");
        updatedDTO.setDescription("Advanced Java concepts");
        updatedDTO.setUserId(1L);

        when(formationRepo.findById(1L)).thenReturn(Optional.of(formation));
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(formationRepo.save(any(Formation.class))).thenReturn(formation);

        // Act
        Formation result = formationService.updateFormation(1L, updatedDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedDTO.getNom(), result.getNom());
        assertEquals(updatedDTO.getDescription(), result.getDescription());
        assertEquals(user, result.getUser());

        verify(formationRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).findById(1);
        verify(formationRepo, times(1)).save(any(Formation.class));
    }

    @Test
    void updateFormation_FormationNotFound_ThrowsException() {
        // Arrange
        when(formationRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            formationService.updateFormation(1L, formationDTO);
        });

        verify(formationRepo, times(1)).findById(1L);
        verify(userRepo, never()).findById(any());
        verify(formationRepo, never()).save(any());
    }

    @Test
    void updateFormation_UserNotFound_ThrowsException() {
        // Arrange
        when(formationRepo.findById(1L)).thenReturn(Optional.of(formation));
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            formationService.updateFormation(1L, formationDTO);
        });

        verify(formationRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).findById(1);
        verify(formationRepo, never()).save(any());
    }

    @Test
    void updateFormation_WithoutUser_Success() {
        // Arrange
        FormationDTO updatedDTO = new FormationDTO();
        updatedDTO.setNom("Advanced Java");
        updatedDTO.setDescription("Advanced Java concepts");
        updatedDTO.setUserId(null);

        when(formationRepo.findById(1L)).thenReturn(Optional.of(formation));
        when(formationRepo.save(any(Formation.class))).thenReturn(formation);

        // Act
        Formation result = formationService.updateFormation(1L, updatedDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedDTO.getNom(), result.getNom());
        assertEquals(updatedDTO.getDescription(), result.getDescription());
        assertEquals(user, result.getUser()); // User should remain unchanged

        verify(formationRepo, times(1)).findById(1L);
        verify(userRepo, never()).findById(any());
        verify(formationRepo, times(1)).save(any(Formation.class));
    }

    @Test
    void getall_ShouldReturnAllFormations() {
        // Arrange
        when(formationRepo.findAll()).thenReturn(List.of(formation));

        // Act
        List<Formation> result = formationService.getall();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(formationRepo, times(1)).findAll();
    }

    @Test
    void getFormationByID_Success() {
        // Arrange
        when(formationRepo.findById(1L)).thenReturn(Optional.of(formation));

        // Act
        Optional<Formation> result = formationService.getFormationByID(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(formation.getId(), result.get().getId());
        verify(formationRepo, times(1)).findById(1L);
    }

    @Test
    void findByUserId_Success() {
        // Arrange
        when(formationRepo.findByUserId(1L)).thenReturn(List.of(formation));

        // Act
        List<Formation> result = formationService.findByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(formationRepo, times(1)).findByUserId(1L);
    }

    @Test
    void getAllFormationsWithUsernames_WithUser() {
        // Arrange
        when(formationRepo.findAll()).thenReturn(List.of(formation));

        // Act
        List<String> result = formationService.getAllFormationsWithUsernames();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).contains("Formation: Java Programming"));
        assertTrue(result.get(0).contains("Utilisateur: testuser"));
    }

    @Test
    void getAllFormationsWithUsernames_WithoutUser() {
        // Arrange
        formation.setUser(null);
        when(formationRepo.findAll()).thenReturn(List.of(formation));

        // Act
        List<String> result = formationService.getAllFormationsWithUsernames();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).contains("Formation: Java Programming"));
        assertTrue(result.get(0).contains("Utilisateur: N/A"));
    }
}