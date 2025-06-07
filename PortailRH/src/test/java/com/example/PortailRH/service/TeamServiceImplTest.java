package com.example.PortailRH.service;

import com.example.PortailRH.entity.Team;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.TeamDTO;
import com.example.PortailRH.entity.enummerations.RoleName;
import com.example.PortailRH.repository.TeamRepo;
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
class TeamServiceImplTest {

    @Mock
    private TeamRepo teamRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private TeamServiceImpl teamService;

    private User chef;
    private User collaborator1;
    private User collaborator2;
    private Team team;

    @BeforeEach
    void setUp() {
        chef = new User();
        chef.setId(1L);
        chef.setUsername("chef1");
        chef.setRoles(Arrays.asList(RoleName.CHEF));

        collaborator1 = new User();
        collaborator1.setId(2L);
        collaborator1.setUsername("collab1");
        collaborator1.setRoles(Arrays.asList(RoleName.COLLABORATEUR));

        collaborator2 = new User();
        collaborator2.setId(3L);
        collaborator2.setUsername("collab2");
        collaborator2.setRoles(Arrays.asList(RoleName.COLLABORATEUR));

        team = new Team();
        team.setId(1L);
        team.setName("Team 1");
        team.setChef(chef);
        team.setCollaborators(new ArrayList<>(Arrays.asList(collaborator1, collaborator2)));
    }

    @Test
    void createTeam_Success() {
        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(userRepo.findAllById(any())).thenReturn(Arrays.asList(collaborator1, collaborator2));
        when(teamRepo.save(any(Team.class))).thenReturn(team);

        Team result = teamService.createTeam(1L, "Team 1", Arrays.asList(2, 3));

        assertNotNull(result);
        assertEquals("Team 1", result.getName());
        assertEquals(chef, result.getChef());
        assertEquals(2, result.getCollaborators().size());
        verify(teamRepo, times(1)).save(any(Team.class));
    }

    @Test
    void createTeam_ChefNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                teamService.createTeam(1L, "Team 1", Arrays.asList(2, 3)));
    }

    @Test
    void createTeam_UserNotChef() {
        User notChef = new User();
        notChef.setId(4L);
        notChef.setRoles(Arrays.asList(RoleName.COLLABORATEUR));

        when(userRepo.findById(4)).thenReturn(Optional.of(notChef));

        assertThrows(RuntimeException.class, () ->
                teamService.createTeam(4L, "Team 1", Arrays.asList(2, 3)));
    }

    @Test
    void getTeamsByChef_Success() {
        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(teamRepo.findByChef(chef)).thenReturn(Collections.singletonList(team));

        List<Team> result = teamService.getTeamsByChef(1L);

        assertEquals(1, result.size());
        assertEquals(team, result.get(0));
    }

    @Test
    void getUserSummariesByTeam_Success() {
        when(teamRepo.findById(1L)).thenReturn(Optional.of(team));

        List<TeamDTO> result = teamService.getUserSummariesByTeam(1L);

        assertEquals(3, result.size()); // 2 collaborators + chef
        assertTrue(result.stream().anyMatch(dto -> "chef1".equals(dto.getName())));
        assertTrue(result.stream().anyMatch(dto -> "collab1".equals(dto.getName())));
        assertTrue(result.stream().anyMatch(dto -> "collab2".equals(dto.getName())));
    }

    @Test
    void getallteam_Success() {
        when(teamRepo.findAll()).thenReturn(Collections.singletonList(team));

        List<Team> result = teamService.getallteam();

        assertEquals(1, result.size());
        assertEquals(team, result.get(0));
    }

    @Test
    void updateTeam_Success() {
        User newCollaborator = new User();
        newCollaborator.setId(4L);
        newCollaborator.setUsername("newCollab");

        when(teamRepo.findById(1L)).thenReturn(Optional.of(team));
        when(userRepo.findAllByUsernameIn(Arrays.asList("newCollab"))).thenReturn(
                Collections.singletonList(newCollaborator));
        when(teamRepo.save(any(Team.class))).thenReturn(team);

        Team result = teamService.updateTeam(1L, "Updated Team",
                Arrays.asList("newCollab"), Arrays.asList("collab1"));

        assertNotNull(result);
        assertEquals("Updated Team", result.getName());
        verify(teamRepo, times(1)).save(team);
    }

    @Test
    void getTeamsWithUserCount_Success() {
        when(teamRepo.findAll()).thenReturn(Collections.singletonList(team));

        List<Map<String, Object>> result = teamService.getTeamsWithUserCount();

        assertEquals(1, result.size());
        assertEquals("Team 1", result.get(0).get("teamName"));
        assertEquals(2, result.get(0).get("userCount"));
    }

    @Test
    void getCollaboratorUsernamesByTeam_Success() {
        when(teamRepo.findById(1L)).thenReturn(Optional.of(team));

        List<Map<String, Object>> result = teamService.getCollaboratorUsernamesByTeam(1L);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(m -> "collab1".equals(m.get("username"))));
        assertTrue(result.stream().anyMatch(m -> "collab2".equals(m.get("username"))));
    }

    @Test
    void getChefInfoByTeam_Success() {
        when(teamRepo.findById(1L)).thenReturn(Optional.of(team));

        Map<String, Object> result = teamService.getChefInfoByTeam(1L);

        assertEquals(1L, result.get("chefId"));
        assertEquals("chef1", result.get("chefUsername"));
    }

    @Test
    void getTeamByTeamManager_Success() {
        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(teamRepo.findByChef(chef)).thenReturn(Collections.singletonList(team));

        Map<String, Object> result = teamService.getTeamByTeamManager(1L);

        assertEquals(1L, result.get("teamId"));
        assertEquals("Team 1", result.get("teamName"));
        assertNotNull(result.get("collaborators"));
    }

    @Test
    void getTeamByCollaboratorId_Success() {
        when(userRepo.findById(2)).thenReturn(Optional.of(collaborator1));
        when(teamRepo.findByCollaboratorsContaining(collaborator1)).thenReturn(Optional.of(team));

        Map<String, Object> result = teamService.getTeamByCollaboratorId(2L);

        assertEquals(1L, result.get("teamId"));
        assertEquals("Team 1", result.get("teamName"));
        assertEquals("chef1", result.get("chefUsername"));
    }

    @Test
    void leaveTeam_Success() {
        when(teamRepo.findById(1L)).thenReturn(Optional.of(team));
        when(userRepo.findById(2)).thenReturn(Optional.of(collaborator1));
        when(teamRepo.save(any(Team.class))).thenReturn(team);

        teamService.leaveTeam(1L, 2L);

        verify(teamRepo, times(1)).save(team);
        assertFalse(team.getCollaborators().contains(collaborator1));
    }

    @Test
    void getTeamInfoByUserId_AsChef() {
        when(userRepo.findById(1)).thenReturn(Optional.of(chef));
        when(teamRepo.findByChef(chef)).thenReturn(Collections.singletonList(team));

        Map<String, Object> result = teamService.getTeamInfoByUserId(1L);

        assertEquals(1L, result.get("teamId"));
        assertEquals("Team 1", result.get("teamName"));
        assertEquals("CHEF", result.get("role"));
    }

    /*@Test
    void getTeamInfoByUserId_AsCollaborator() {
        when(userRepo.findById(2)).thenReturn(Optional.of(collaborator1));
        when(teamRepo.findByCollaboratorsContaining(collaborator1)).thenReturn(Optional.of(team));

        Map<String, Object> result = teamService.getTeamInfoByUserId(2L);

        assertEquals(1L, result.get("teamId"));
        assertEquals("Team 1", result.get("teamName"));
        assertEquals("COLLABORATEUR", result.get("role"));
    }*/

    @Test
    void getTeamInfoByUserId_NoTeam() {
        User user = new User();
        user.setId(4L);
        when(userRepo.findById(4)).thenReturn(Optional.of(user));

        Map<String, Object> result = teamService.getTeamInfoByUserId(4L);

        assertEquals("User does not belong to any team", result.get("message"));
        assertEquals(4L, result.get("userId"));
    }
}