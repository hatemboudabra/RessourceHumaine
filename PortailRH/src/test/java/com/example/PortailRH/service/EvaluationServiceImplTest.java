package com.example.PortailRH.service;

import com.example.PortailRH.entity.*;
import com.example.PortailRH.entity.dto.EvaluationDTO;
import com.example.PortailRH.entity.enummerations.ComplexiteTache;
import com.example.PortailRH.repository.*;
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
class EvaluationServiceImplTest {

    @Mock
    private EvaluationRepo evaluationRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private TacheRepo tacheRepo;

    @Mock
    private TeamRepo teamRepo;

    @InjectMocks
    private EvaluationServiceImpl evaluationService;

    private EvaluationDTO evaluationDTO;
    private Evaluation evaluation;
    private User user;
    private User chef;
    private Tache tache;
    private Team team;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        chef = new User();
        chef.setId(2L);
        chef.setUsername("chefuser");

        evaluationDTO = new EvaluationDTO();
        evaluationDTO.setComment("Good performance");
        evaluationDTO.setNote(4L);
        evaluationDTO.setUserId(1L);

        evaluation = new Evaluation();
        evaluation.setId(1L);
        evaluation.setComment("Good performance");
        evaluation.setNote(4L);
        evaluation.setUser(user);

        tache = new Tache();
        tache.setId(1L);
        tache.setUser(user);
        tache.setComplexite(ComplexiteTache.INTERMEDIAIRE);

        team = new Team();
        team.setId(1L);
        team.setChef(chef);
        team.setCollaborators(Collections.singletonList(user));
    }

    @Test
    void addEvaluation_Success() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(evaluationRepo.save(any(Evaluation.class))).thenReturn(evaluation);

        // Act
        Evaluation result = evaluationService.addEvaluation(evaluationDTO);

        // Assert
        assertNotNull(result);
        assertEquals(evaluationDTO.getComment(), result.getComment());
        assertEquals(evaluationDTO.getNote(), result.getNote());
        assertEquals(user, result.getUser());

        verify(userRepo, times(1)).findById(1);
        verify(evaluationRepo, times(1)).save(any(Evaluation.class));
    }

    @Test
    void addEvaluation_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            evaluationService.addEvaluation(evaluationDTO);
        });

        verify(userRepo, times(1)).findById(1);
        verify(evaluationRepo, never()).save(any());
    }

    @Test
    void updateEvaluation_Success() {
        // Arrange
        when(evaluationRepo.findById(1L)).thenReturn(Optional.of(evaluation));
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(evaluationRepo.save(any(Evaluation.class))).thenReturn(evaluation);

        // Act
        Evaluation result = evaluationService.updateEvaluation(1L, evaluationDTO);

        // Assert
        assertNotNull(result);
        assertEquals(evaluationDTO.getComment(), result.getComment());
        assertEquals(evaluationDTO.getNote(), result.getNote());

        verify(evaluationRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).findById(1);
        verify(evaluationRepo, times(1)).save(any(Evaluation.class));
    }

    @Test
    void updateEvaluation_EvaluationNotFound_ThrowsException() {
        // Arrange
        when(evaluationRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            evaluationService.updateEvaluation(1L, evaluationDTO);
        });

        verify(evaluationRepo, times(1)).findById(1L);
        verify(userRepo, never()).findById(any());
        verify(evaluationRepo, never()).save(any());
    }

    @Test
    void getallEvaluation_Success() {
        // Arrange
        when(evaluationRepo.findAll()).thenReturn(Collections.singletonList(evaluation));

        // Act
        List<Evaluation> result = evaluationService.getallEvaluation();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(evaluationRepo, times(1)).findAll();
    }

    @Test
    void getEvaluationById_Success() {
        // Arrange
        when(evaluationRepo.findById(1L)).thenReturn(Optional.of(evaluation));

        // Act
        Optional<Evaluation> result = evaluationService.getEvaluationById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(evaluation.getId(), result.get().getId());
        verify(evaluationRepo, times(1)).findById(1L);
    }

    @Test
    void deleteEvaluation_Success() {
        // Arrange
        doNothing().when(evaluationRepo).deleteById(1L);

        // Act
        evaluationService.deleteEvaluation(1L);

        // Assert
        verify(evaluationRepo, times(1)).deleteById(1L);
    }

    @Test
    void evaluateUser_Success() {
        // Arrange
        when(userRepo.findById(2)).thenReturn(Optional.of(chef));
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(teamRepo.findByChef(chef)).thenReturn(Collections.singletonList(team));
        when(tacheRepo.findByUser(user)).thenReturn(Collections.singletonList(tache));
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(evaluationRepo.save(any(Evaluation.class))).thenReturn(evaluation);

        // Act
        evaluationService.evaluateUser(2L, 1L);

        // Assert
        assertEquals(2.0, user.getNoteGlobale());
        verify(userRepo, times(1)).save(user);
        verify(evaluationRepo, times(1)).save(any(Evaluation.class));
    }

    @Test
    void evaluateUser_ChefNotFound_ThrowsException() {
        // Arrange
        when(userRepo.findById(2)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            evaluationService.evaluateUser(2L, 1L);
        });

        verify(userRepo, times(1)).findById(2);
        verify(userRepo, never()).save(any());
    }

    @Test
    void evaluateUser_CollaboratorNotFound_ThrowsException() {
        // Arrange
        when(userRepo.findById(2)).thenReturn(Optional.of(chef));
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            evaluationService.evaluateUser(2L, 1L);
        });

        verify(userRepo, times(1)).findById(1);
        verify(userRepo, never()).save(any());
    }

    @Test
    void evaluateUser_ChefHasNoTeam_ThrowsException() {
        // Arrange
        when(userRepo.findById(2)).thenReturn(Optional.of(chef));
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(teamRepo.findByChef(chef)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            evaluationService.evaluateUser(2L, 1L);
        });

        verify(teamRepo, times(1)).findByChef(chef);
        verify(userRepo, never()).save(any());
    }

    @Test
    void evaluateUser_CollaboratorNotInTeam_ThrowsException() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(3L);
        team.setCollaborators(Collections.singletonList(otherUser));

        when(userRepo.findById(2)).thenReturn(Optional.of(chef));
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(teamRepo.findByChef(chef)).thenReturn(Collections.singletonList(team));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            evaluationService.evaluateUser(2L, 1L);
        });

        verify(teamRepo, times(1)).findByChef(chef);
        verify(userRepo, never()).save(any());
    }

    @Test
    void evaluateUser_NoTasksAssigned_ThrowsException() {
        // Arrange
        when(userRepo.findById(2)).thenReturn(Optional.of(chef));
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(teamRepo.findByChef(chef)).thenReturn(Collections.singletonList(team));
        when(tacheRepo.findByUser(user)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            evaluationService.evaluateUser(2L, 1L);
        });

        verify(tacheRepo, times(1)).findByUser(user);
        verify(userRepo, never()).save(any());
    }

    @Test
    void getCollaboratorEvaluationNote_WithEvaluations() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(evaluationRepo.findByUser(user)).thenReturn(Collections.singletonList(evaluation));

        // Act
        String result = evaluationService.getCollaboratorEvaluationNote(1L);

        // Assert
        assertEquals("4", result);
        verify(evaluationRepo, times(1)).findByUser(user);
    }

    @Test
    void getCollaboratorEvaluationNote_NoEvaluations() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(evaluationRepo.findByUser(user)).thenReturn(Collections.emptyList());

        // Act
        String result = evaluationService.getCollaboratorEvaluationNote(1L);

        // Assert
        assertEquals(" ", result);
        verify(evaluationRepo, times(1)).findByUser(user);
    }

    @Test
    void getCollaboratorEvaluationNote_UserNotFound() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            evaluationService.getCollaboratorEvaluationNote(1L);
        });

        verify(userRepo, times(1)).findById(1);
        verify(evaluationRepo, never()).findByUser(any());
    }

    @Test
    void getAllUsersWithAverageNote_Success() {
        // Arrange
        Object[] result1 = new Object[]{"user1", 4.5};
        Object[] result2 = new Object[]{"user2", 3.8};
        when(evaluationRepo.findAverageNoteForAllUsers()).thenReturn(Arrays.asList(result1, result2));

        // Act
        List<Map<String, Object>> result = evaluationService.getAllUsersWithAverageNote();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).get("username"));
        assertEquals(4.5, result.get(0).get("averageNote"));
        verify(evaluationRepo, times(1)).findAverageNoteForAllUsers();
    }
}