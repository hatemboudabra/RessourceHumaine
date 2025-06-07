package com.example.PortailRH.service;

import com.example.PortailRH.entity.*;
import com.example.PortailRH.entity.dto.NotificationRequest;
import com.example.PortailRH.entity.dto.TacheDTO;
import com.example.PortailRH.entity.enummerations.StatusTache;
import com.example.PortailRH.repository.ProjectRepo;
import com.example.PortailRH.repository.TacheRepo;
import com.example.PortailRH.repository.TeamRepo;
import com.example.PortailRH.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TacheServiceImplTest {

    @Mock
    private TacheRepo tacheRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private TeamRepo teamRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ProjectRepo projectRepo;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private TacheServiceImpl tacheService;

    private User chef;
    private User collaborator;
    private Tache tache;
    private TacheDTO tacheDTO;
    private Team team;
    private Projet projet;

    @BeforeEach
    void setUp() {
        chef = new User();
        chef.setId(1L); // Changé en Long
        chef.setUsername("chef1");
        chef.setEmail("chef@example.com");

        collaborator = new User();
        collaborator.setId(2L); // Changé en Long
        collaborator.setUsername("collab1");
        collaborator.setEmail("collab@example.com");

        projet = new Projet();
        projet.setId(1L);
        projet.setName("Project 1");

        tache = new Tache();
        tache.setId(1L);
        tache.setTitle("Test Task");
        tache.setDescription("Test Description");
        tache.setDateDebut(new Date());
        tache.setDateFin(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));
        tache.setStatusTache(StatusTache.NOT_ASSIGNED);
        tache.setCreatedBy(chef);
        tache.setProjet(projet);

        tacheDTO = new TacheDTO();
        tacheDTO.setTitle("Test Task");
        tacheDTO.setDescription("Test Description");
        tacheDTO.setDateDebut(new Date());
        tacheDTO.setDateFin(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));
        tacheDTO.setStatusTache(StatusTache.NOT_ASSIGNED);
        tacheDTO.setProjectId(1L);

        team = new Team();
        team.setId(1L);
        team.setChef(chef);
        team.setCollaborators(Collections.singletonList(collaborator));
    }

    @Test
    void addTacheByChef_Success() {
        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(projectRepo.findById(1L)).thenReturn(Optional.of(projet));
        when(tacheRepo.save(any(Tache.class))).thenReturn(tache);

        Tache result = tacheService.addTacheByChef(1L, tacheDTO);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals(chef, result.getCreatedBy());
        assertEquals(StatusTache.NOT_ASSIGNED, result.getStatusTache());
        verify(tacheRepo, times(1)).save(any(Tache.class));
    }

    @Test
    void addTacheByChef_ChefNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tacheService.addTacheByChef(1L, tacheDTO));
    }

    @Test
    void getallTache_Success() {
        List<Tache> taches = Collections.singletonList(tache);
        when(tacheRepo.findAll()).thenReturn(taches);

        List<Tache> result = tacheService.getallTache();

        assertEquals(1, result.size());
        assertEquals(tache, result.get(0));
        verify(tacheRepo, times(1)).findAll();
    }

    @Test
    void getTacheById_Success() {
        when(tacheRepo.findById(1L)).thenReturn(Optional.of(tache));

        Optional<Tache> result = tacheService.getTacheById(1L);

        assertTrue(result.isPresent());
        assertEquals(tache, result.get());
    }

    @Test
    void getTacheById_NotFound() {
        when(tacheRepo.findById(1L)).thenReturn(Optional.empty());

        Optional<Tache> result = tacheService.getTacheById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateStatus_Success() {
        when(tacheRepo.findById(1L)).thenReturn(Optional.of(tache));
        when(tacheRepo.save(any(Tache.class))).thenReturn(tache);

        Tache result = tacheService.updateStatus(1L, StatusTache.INPROGRESS);

        assertNotNull(result);
        assertEquals(StatusTache.INPROGRESS, result.getStatusTache());
        verify(tacheRepo, times(1)).save(tache);
    }

    @Test
    void updateStatus_TacheNotFound() {
        when(tacheRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tacheService.updateStatus(1L, StatusTache.INPROGRESS));
    }

    @Test
    void assignTacheToCollaborator_Success() {
        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(userRepo.findById(2)).thenReturn(Optional.of(collaborator));
        when(tacheRepo.findById(1L)).thenReturn(Optional.of(tache));
        when(teamRepository.findByChef(chef)).thenReturn(Collections.singletonList(team));
        when(tacheRepo.save(any(Tache.class))).thenReturn(tache);

        Tache result = tacheService.assignTacheToCollaborator(1L, 1L, 2L);

        assertNotNull(result);
        assertEquals(collaborator, result.getUser());
        assertEquals(StatusTache.ASSIGNED, result.getStatusTache());
        verify(tacheRepo, times(1)).save(tache);
        verify(notificationClient, times(1)).sendNotification(eq(2L), any(NotificationRequest.class));
    }

    @Test
    void assignTacheToCollaborator_ChefNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tacheService.assignTacheToCollaborator(1L, 1L, 2L));
    }

    @Test
    void assignTacheToCollaborator_CollaboratorNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(userRepo.findById(2)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tacheService.assignTacheToCollaborator(1L, 1L, 2L));
    }

    @Test
    void assignTacheToCollaborator_TacheNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(userRepo.findById(2)).thenReturn(Optional.of(collaborator));
        when(tacheRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tacheService.assignTacheToCollaborator(1L, 1L, 2L));
    }

    @Test
    void assignTacheToCollaborator_NotInSameTeam() {
        Team differentTeam = new Team();
        differentTeam.setId(2L);
        differentTeam.setChef(chef);
        differentTeam.setCollaborators(Collections.emptyList());

        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(userRepo.findById(2)).thenReturn(Optional.of(collaborator));
        when(tacheRepo.findById(1L)).thenReturn(Optional.of(tache));
        when(teamRepository.findByChef(chef)).thenReturn(Collections.singletonList(differentTeam));

        assertThrows(RuntimeException.class, () -> tacheService.assignTacheToCollaborator(1L, 1L, 2L));
    }

    @Test
    void gettacheByChef_Success() {
        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(tacheRepo.findAllByCreatedBy(chef)).thenReturn(Collections.singletonList(tache));

        List<Tache> result = tacheService.gettacheByChef(1L);

        assertEquals(1, result.size());
        assertEquals(tache, result.get(0));
        verify(tacheRepo, times(1)).findAllByCreatedBy(chef);
    }

    @Test
    void getAssignedTachesByChef_Success() {
        tache.setUser(collaborator);
        tache.setStatusTache(StatusTache.ASSIGNED);

        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(userRepo.findById(2)).thenReturn(Optional.of(collaborator));
        when(teamRepository.findByChef(chef)).thenReturn(Collections.singletonList(team));
        when(tacheRepo.findAllByCreatedByAndUserAndStatusTache(chef, collaborator, StatusTache.ASSIGNED))
                .thenReturn(Collections.singletonList(tache));

        List<Tache> result = tacheService.getAssignedTachesByChef(1L, 2L);

        assertEquals(1, result.size());
        assertEquals(tache, result.get(0));
    }

    @Test
    void getAssignedCollaboratorUsername_Success() {
        tache.setUser(collaborator);
        when(tacheRepo.findById(1L)).thenReturn(Optional.of(tache));

        String result = tacheService.getAssignedCollaboratorUsername(1L);

        assertEquals("collab1", result);
    }

    @Test
    void getAssignedCollaboratorUsername_NoCollaborator() {
        when(tacheRepo.findById(1L)).thenReturn(Optional.of(tache));

        assertThrows(RuntimeException.class, () -> tacheService.getAssignedCollaboratorUsername(1L));
    }

    @Test
    void countTachesByStatusAndChef_Success() {
        List<Tache> taches = Arrays.asList(
                createTacheWithStatus(StatusTache.NOT_ASSIGNED),
                createTacheWithStatus(StatusTache.ASSIGNED),
                createTacheWithStatus(StatusTache.ASSIGNED)
        );

        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(tacheRepo.findAllByCreatedBy(chef)).thenReturn(taches);

        Map<StatusTache, Long> result = tacheService.countTachesByStatusAndChef(1L);

        assertEquals(2, result.size());
        assertEquals(1, result.get(StatusTache.NOT_ASSIGNED));
        assertEquals(2, result.get(StatusTache.ASSIGNED));
    }

    @Test
    void countTachesAssignedByChefToCollaborators_Success() {
        User collaborator2 = new User();
        collaborator2.setId(3L); // Changé en Long
        collaborator2.setUsername("collab2");

        List<Tache> taches = Arrays.asList(
                createTacheWithUser(collaborator),
                createTacheWithUser(collaborator),
                createTacheWithUser(collaborator2)
        );

        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(tacheRepo.findAllByCreatedBy(chef)).thenReturn(taches);

        Map<String, Long> result = tacheService.countTachesAssignedByChefToCollaborators(1L);

        assertEquals(2, result.size());
        assertEquals(2, result.get("collab1"));
        assertEquals(1, result.get("collab2"));
    }

    @Test
    void getTachesByCollaboratorId_Success() {
        when(userRepo.findById(2)).thenReturn(Optional.of(collaborator));
        when(tacheRepo.findAllByUser(collaborator)).thenReturn(Collections.singletonList(tache));

        List<Tache> result = tacheService.getTachesByCollaboratorId(2L);

        assertEquals(1, result.size());
        assertEquals(tache, result.get(0));
    }

    @Test
    void deleteTache_Success() {
        doNothing().when(tacheRepo).deleteById(1L);

        tacheService.DeleteTache(1L);

        verify(tacheRepo, times(1)).deleteById(1L);
    }

    private Tache createTacheWithStatus(StatusTache status) {
        Tache t = new Tache();
        t.setStatusTache(status);
        t.setCreatedBy(chef);
        return t;
    }

    private Tache createTacheWithUser(User user) {
        Tache t = new Tache();
        t.setUser(user);
        t.setCreatedBy(chef);
        return t;
    }
}