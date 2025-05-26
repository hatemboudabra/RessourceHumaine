package com.example.PortailRH.controller;

import com.example.PortailRH.entity.Team;
import com.example.PortailRH.entity.dto.TeamDTO;
import com.example.PortailRH.service.TeamService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }
    @PostMapping("/createTeam")
    public ResponseEntity<Team> createTeam(
            @RequestParam Long chefId,
            @RequestParam String teamName,
            @RequestBody List<Integer> collaboratorIds) {
        try {
            Team team = teamService.createTeam(chefId, teamName, collaboratorIds);
            return ResponseEntity.ok(team);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
    @GetMapping("/by-chef")
    public ResponseEntity<List<Team>> getTeamsByChef(@RequestParam Long chefId) {
        List<Team> teams = teamService.getTeamsByChef(chefId);
        return ResponseEntity.ok(teams);
    }
    @GetMapping("/{teamId}/users")
    public ResponseEntity<List<TeamDTO>> getUserSummariesByTeam(@PathVariable Long teamId) {
        List<TeamDTO> team = teamService.getUserSummariesByTeam(teamId);
        return ResponseEntity.ok(team);
    }


    @GetMapping("/allTeam")
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamService.getallteam();
        return ResponseEntity.ok(teams);
    }
    @PutMapping("/updateTeam")
    public ResponseEntity<Team> updateTeam(
            @RequestParam Long teamId,
            @RequestParam(required = false) String newTeamName,
            @RequestParam(required = false) List<String> collaboratorUsernamesToAdd,
            @RequestParam(required = false) List<String> collaboratorUsernamesToRemove) {
        try {
            Team updatedTeam = teamService.updateTeam(teamId, newTeamName, collaboratorUsernamesToAdd, collaboratorUsernamesToRemove);
            return ResponseEntity.ok(updatedTeam);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @GetMapping("/teamsusercount")
    public List<Map<String, Object>> getTeamsWithUserCount() {
        return teamService.getTeamsWithUserCount();
    }
    @GetMapping("/{teamId}/collaborators")
    public ResponseEntity<List<Map<String, Object>>> getCollaboratorsByTeam(@PathVariable Long teamId) {
        try {
            List<Map<String, Object>> collaborators = teamService.getCollaboratorUsernamesByTeam(teamId);
            return ResponseEntity.ok(collaborators);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{teamId}/chefinfo")
    public ResponseEntity<Map<String, Object>> getChefInfoByTeam(@PathVariable Long teamId) {
        try {
            Map<String, Object> chefInfo = teamService.getChefInfoByTeam(teamId);
            return ResponseEntity.ok(chefInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
    }

}

    @GetMapping("/team/{chefId}")
    public ResponseEntity<Map<String, Object>> getTeamByTeamManager(@PathVariable Long chefId) {
        Map<String, Object> teamData = teamService.getTeamByTeamManager(chefId);
        return ResponseEntity.ok(teamData);
    }
    @GetMapping("/bycollaborator/{collaboratorId}")
    public Map<String, Object> getTeamByCollaboratorId(@PathVariable Long collaboratorId) {
        return teamService.getTeamByCollaboratorId(collaboratorId);
    }

    @DeleteMapping("/{teamId}/collaborators/{collaboratorId}")
    public ResponseEntity<String> leaveTeam(
            @PathVariable Long teamId,
            @PathVariable Long collaboratorId) {
        try {
            teamService.leaveTeam(teamId, collaboratorId);
            return ResponseEntity.ok("Collaborator has successfully left the team.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/chatteam/{userId}")
    public Map<String, Object> getTeamInfoByUserId(@PathVariable Long userId) {
        return teamService.getTeamInfoByUserId(userId);
    }
}

