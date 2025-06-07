package com.example.PortailRH.service;

import com.example.PortailRH.entity.Reclamation;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.ReclamationDTO;
import com.example.PortailRH.repository.ReclamationRepo;
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
class ReclamationServiceImplTest {

    @Mock
    private ReclamationRepo reclamationRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private ReclamationServiceImpl reclamationService;

    private ReclamationDTO reclamationDTO;
    private Reclamation reclamation;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        reclamationDTO = new ReclamationDTO();
        reclamationDTO.setTitle("Test Reclamation");
        reclamationDTO.setDescription("Test Description");
        reclamationDTO.setUserId(1L);

        reclamation = new Reclamation();
        reclamation.setId(1L);
        reclamation.setTitle("Test Reclamation");
        reclamation.setDescription("Test Description");
        reclamation.setUser(user);
    }

    @Test
    void addReclamation_Success() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(reclamationRepo.save(any(Reclamation.class))).thenReturn(reclamation);

        // Act
        Reclamation result = reclamationService.addReclamation(reclamationDTO);

        // Assert
        assertNotNull(result);
        assertEquals(reclamationDTO.getTitle(), result.getTitle());
        assertEquals(reclamationDTO.getDescription(), result.getDescription());
        assertEquals(user, result.getUser());

        verify(userRepo, times(1)).findById(1);
        verify(reclamationRepo, times(1)).save(any(Reclamation.class));
    }

    @Test
    void addReclamation_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            reclamationService.addReclamation(reclamationDTO);
        });

        verify(userRepo, times(1)).findById(1);
        verify(reclamationRepo, never()).save(any());
    }

    @Test
    void deleteReclamation_Success() {
        // Arrange
        doNothing().when(reclamationRepo).deleteById(1L);

        // Act
        reclamationService.DeleteReclamation(1L);

        // Assert
        verify(reclamationRepo, times(1)).deleteById(1L);
    }

    @Test
    void getallReclamation_ShouldReturnAllReclamations() {
        // Arrange
        when(reclamationRepo.findAll()).thenReturn(List.of(reclamation));

        // Act
        List<Reclamation> result = reclamationService.getallReclamation();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reclamationRepo, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        // Arrange
        when(reclamationRepo.findById(1L)).thenReturn(Optional.of(reclamation));

        // Act
        Optional<Reclamation> result = reclamationService.getById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(reclamation.getId(), result.get().getId());
        verify(reclamationRepo, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound_ReturnsEmptyOptional() {
        // Arrange
        when(reclamationRepo.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Reclamation> result = reclamationService.getById(1L);

        // Assert
        assertTrue(result.isEmpty());
        verify(reclamationRepo, times(1)).findById(1L);
    }

    @Test
    void findByUserId_Success() {
        // Arrange
        when(reclamationRepo.findByUserId(1L)).thenReturn(List.of(reclamation));

        // Act
        List<Reclamation> result = reclamationService.findByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reclamationRepo, times(1)).findByUserId(1L);
    }

    @Test
    void findByUserId_NoResults_ReturnsEmptyList() {
        // Arrange
        when(reclamationRepo.findByUserId(1L)).thenReturn(List.of());

        // Act
        List<Reclamation> result = reclamationService.findByUserId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reclamationRepo, times(1)).findByUserId(1L);
    }
}