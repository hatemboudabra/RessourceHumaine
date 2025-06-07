package com.example.PortailRH.service;

import com.example.PortailRH.entity.Competance;
import com.example.PortailRH.entity.Formation;
import com.example.PortailRH.entity.enummerations.NiveauC;
import com.example.PortailRH.entity.dto.CompetanceDTO;
import com.example.PortailRH.repository.CompetanceRepo;
import com.example.PortailRH.repository.FormationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompetanceServiceImplTest {

    @Mock
    private CompetanceRepo competanceRepo;

    @Mock
    private FormationRepo formationRepo;

    @InjectMocks
    private CompetanceServiceImpl competanceService;

    private CompetanceDTO competanceDTO;
    private Competance competance;
    private Formation formation;

    @BeforeEach
    void setUp() {
        formation = new Formation();
        formation.setId(1L);
        formation.setNom("Formation Test");

        competanceDTO = new CompetanceDTO();
        competanceDTO.setNom("Java");
        competanceDTO.setNiveauC(NiveauC.EXPERT);
        competanceDTO.setFormationId(1L);

        competance = new Competance();
        competance.setId(1L);
        competance.setNom("Java");
        competance.setNiveauC(NiveauC.EXPERT);
        competance.setFormation(formation);
    }

    @Test
    void addCompetance_Success() {
        // Arrange
        when(formationRepo.findById(1L)).thenReturn(Optional.of(formation));
        when(competanceRepo.save(any(Competance.class))).thenReturn(competance);

        // Act
        Competance result = competanceService.addComprtance(competanceDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Java", result.getNom());
        assertEquals(NiveauC.EXPERT, result.getNiveauC());
        assertEquals(formation, result.getFormation());

        verify(formationRepo, times(1)).findById(1L);
        verify(competanceRepo, times(1)).save(any(Competance.class));
    }

    @Test
    void updateCompetance_Success() {
        // Arrange
        Long id = 1L;
        CompetanceDTO updatedDTO = new CompetanceDTO();
        updatedDTO.setNom("Python");
        updatedDTO.setNiveauC(NiveauC.INTERMEDIATE);
        updatedDTO.setFormationId(1L);

        when(competanceRepo.findById(id)).thenReturn(Optional.of(competance));
        when(formationRepo.findById(1L)).thenReturn(Optional.of(formation));
        when(competanceRepo.save(any(Competance.class))).thenReturn(competance);

        // Act
        Competance result = competanceService.updateCompetance(id, updatedDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Python", result.getNom());
        assertEquals(NiveauC.INTERMEDIATE, result.getNiveauC());
        assertEquals(formation, result.getFormation());

        verify(competanceRepo, times(1)).findById(id);
        verify(formationRepo, times(1)).findById(1L);
        verify(competanceRepo, times(1)).save(any(Competance.class));
    }

    @Test
    void updateCompetance_NotFound_ThrowsException() {
        // Arrange
        Long id = 99L;
        when(competanceRepo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            competanceService.updateCompetance(id, competanceDTO);
        });

        verify(competanceRepo, times(1)).findById(id);
        verify(formationRepo, never()).findById(any());
        verify(competanceRepo, never()).save(any());
    }

    @Test
    void deleteCompetance_Success() {
        // Arrange
        Long id = 1L;
        doNothing().when(competanceRepo).deleteById(id);

        // Act
        competanceService.deleteCompetance(id);

        // Assert
        verify(competanceRepo, times(1)).deleteById(id);
    }

    @Test
    void getAllCompetances_Success() {
        // Arrange
        Competance competance2 = new Competance();
        competance2.setId(2L);
        competance2.setNom("Spring Boot");
        competance2.setNiveauC(NiveauC.EXPERT);

        List<Competance> expectedCompetances = Arrays.asList(competance, competance2);
        when(competanceRepo.findAll()).thenReturn(expectedCompetances);

        // Act
        List<Competance> result = competanceService.getallCompetance();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(competanceRepo, times(1)).findAll();
    }

    @Test
    void addCompetance_FormationNotFound_ThrowsException() {
        // Arrange
        when(formationRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            competanceService.addComprtance(competanceDTO);
        });

        verify(formationRepo, times(1)).findById(1L);
        verify(competanceRepo, never()).save(any());
    }

    @Test
    void updateCompetance_FormationNotFound_ThrowsException() {
        // Arrange
        Long id = 1L;
        CompetanceDTO updatedDTO = new CompetanceDTO();
        updatedDTO.setFormationId(99L);

        when(competanceRepo.findById(id)).thenReturn(Optional.of(competance));
        when(formationRepo.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            competanceService.updateCompetance(id, updatedDTO);
        });

        verify(competanceRepo, times(1)).findById(id);
        verify(formationRepo, times(1)).findById(99L);
        verify(competanceRepo, never()).save(any());
    }
}