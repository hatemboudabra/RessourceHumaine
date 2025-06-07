package com.example.PortailRH.service;

import com.example.PortailRH.entity.Projet;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.ProjectDTO;
import com.example.PortailRH.repository.ProjectRepo;
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
class ProjectServiceImplTest {

    @Mock
    private ProjectRepo projectRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Projet projet;
    private ProjectDTO projectDTO;
    private User chef;

    @BeforeEach
    void setUp() {
        chef = new User();
        chef.setId(1L);
        chef.setUsername("chef1");

        projet = new Projet();
        projet.setId(1L);
        projet.setName("Project 1");
        projet.setDescription("Description 1");
        projet.setChef(chef);

        projectDTO = new ProjectDTO();
        projectDTO.setName("Project 1");
        projectDTO.setDescription("Description 1");
        projectDTO.setChefId(1L);
    }

    @Test
    void getAllProjet_Success() {
        when(projectRepo.findAll()).thenReturn(List.of(projet));

        List<Projet> result = projectService.getAllProjet();

        assertEquals(1, result.size());
        assertEquals(projet, result.get(0));
        verify(projectRepo, times(1)).findAll();
    }

    @Test
    void getProjetById_Success() {
        when(projectRepo.findById(1L)).thenReturn(Optional.of(projet));

        Projet result = projectService.getProjetById(1L);

        assertNotNull(result);
        assertEquals(projet, result);
        verify(projectRepo, times(1)).findById(1L);
    }

    @Test
    void getProjetById_NotFound() {
        when(projectRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.getProjetById(1L));
    }

    @Test
    void addProjet_Success() {
        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(projectRepo.save(any(Projet.class))).thenReturn(projet);

        Projet result = projectService.addProjet(projectDTO);

        assertNotNull(result);
        assertEquals("Project 1", result.getName());
        assertEquals(chef, result.getChef());
        verify(projectRepo, times(1)).save(any(Projet.class));
    }

    @Test
    void addProjet_ChefNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.addProjet(projectDTO));
    }

    @Test
    void findByChefId_Success() {
        when(projectRepo.findByChefId(1L)).thenReturn(List.of(projet));

        List<Projet> result = projectService.findByChefId(1L);

        assertEquals(1, result.size());
        assertEquals(projet, result.get(0));
        verify(projectRepo, times(1)).findByChefId(1L);
    }

    @Test
    void deleteProjetById_Success() {
        doNothing().when(projectRepo).deleteById(1L);

        projectService.deleteProjetById(1L);

        verify(projectRepo, times(1)).deleteById(1L);
    }

    @Test
    void updateProjet_Success() {
        ProjectDTO updatedDTO = new ProjectDTO();
        updatedDTO.setName("Updated Project");
        updatedDTO.setDescription("Updated Description");
        updatedDTO.setChefId(2L);

        User newChef = new User();
        newChef.setId(2L);
        newChef.setUsername("chef2");

        when(projectRepo.findById(1L)).thenReturn(Optional.of(projet));
        when(userRepo.findById(2)).thenReturn(Optional.of(newChef));
        when(projectRepo.save(any(Projet.class))).thenReturn(projet);

        Projet result = projectService.updateProjet(1L, updatedDTO);

        assertNotNull(result);
        assertEquals("Updated Project", result.getName());
        assertEquals(newChef, result.getChef());
        verify(projectRepo, times(1)).save(projet);
    }

    @Test
    void updateProjet_ProjectNotFound() {
        when(projectRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.updateProjet(1L, projectDTO));
    }

    @Test
    void updateProjet_ChefNotFound() {
        when(projectRepo.findById(1L)).thenReturn(Optional.of(projet));
        when(userRepo.findById(2)).thenReturn(Optional.empty());

        ProjectDTO updatedDTO = new ProjectDTO();
        updatedDTO.setChefId(2L);

        assertThrows(RuntimeException.class, () -> projectService.updateProjet(1L, updatedDTO));
    }

    @Test
    void updateProjet_WithoutChangingChef() {
        ProjectDTO updatedDTO = new ProjectDTO();
        updatedDTO.setName("Updated Project");
        updatedDTO.setDescription("Updated Description");

        when(projectRepo.findById(1L)).thenReturn(Optional.of(projet));
        when(projectRepo.save(any(Projet.class))).thenReturn(projet);

        Projet result = projectService.updateProjet(1L, updatedDTO);

        assertNotNull(result);
        assertEquals("Updated Project", result.getName());
        assertEquals(chef, result.getChef()); // Chef remains unchanged
        verify(projectRepo, times(1)).save(projet);
    }
}