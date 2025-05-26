package com.example.PortailRH.service;

import com.example.PortailRH.entity.Team;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.TeamDTO;
import com.example.PortailRH.entity.enummerations.RoleName;
import com.example.PortailRH.repository.TeamRepo;
import com.example.PortailRH.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements TeamService{
    private final TeamRepo teamRepo;
    private final UserRepo userRepo;
  //  private final UserService userService;
    public TeamServiceImpl(TeamRepo teamRepo, UserRepo userRepo, UserService userService) {
        this.teamRepo = teamRepo;
        this.userRepo = userRepo;
    }


    @Override
    public Team createTeam(Long chefId, String teamName, List<Integer> collaboratorIds) {
        User chef = userRepo.findById(Math.toIntExact(chefId))
                .orElseThrow(() -> new RuntimeException("Chef not found"));
        if (!chef.getRoles().contains(RoleName.CHEF)) {
            throw new RuntimeException("User is not authorized to create a team");
        }
        Team team = new Team();
        team.setName(teamName);
        team.setChef(chef);

        List<User> collaborators = userRepo.findAllById(collaboratorIds);
        team.setCollaborators(collaborators);

        return teamRepo.save(team);
    }

    @Override
    public List<Team> getTeamsByChef(Long chefId) {
        User chef = userRepo.findById(Math.toIntExact(chefId))
                .orElseThrow(() -> new RuntimeException("Chef not found"));
       return teamRepo.findByChef(chef);
    }

    @Override
    public List<TeamDTO> getUserSummariesByTeam(Long teamId) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with ID: " + teamId));
        List<TeamDTO> users = team.getCollaborators().stream()
                .map(user -> new TeamDTO(user.getUsername(), user.getRoles()))
                .collect(Collectors.toList());
        User chef = team.getChef();
        users.add(new TeamDTO(chef.getUsername(), chef.getRoles()));

        return users;
    }

    @Override
    public List<Team> getallteam() {
        return teamRepo.findAll();
    }

    @Override
    public Team updateTeam(Long teamId, String newTeamName, List<String> collaboratorUsernamesToAdd, List<String> collaboratorUsernamesToRemove) {
        Team existingTeam = teamRepo.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + teamId));

        if (newTeamName != null && !newTeamName.isBlank()) {
            existingTeam.setName(newTeamName);
        }

        if (collaboratorUsernamesToAdd != null && !collaboratorUsernamesToAdd.isEmpty()) {
            List<User> collaboratorsToAdd = userRepo.findAllByUsernameIn(collaboratorUsernamesToAdd);
            existingTeam.getCollaborators().addAll(collaboratorsToAdd);
        }

        if (collaboratorUsernamesToRemove != null && !collaboratorUsernamesToRemove.isEmpty()) {
            List<User> collaboratorsToRemove = userRepo.findAllByUsernameIn(collaboratorUsernamesToRemove);
            existingTeam.getCollaborators().removeAll(collaboratorsToRemove);
        }

        return teamRepo.save(existingTeam);
    }
    @Override
    public List<Map<String, Object>> getTeamsWithUserCount() {
        List<Team> allTeams = teamRepo.findAll();
        return allTeams.stream()
                .map(team -> {
                    Map<String, Object> teamInfo = new HashMap<>();
                    teamInfo.put("teamName", team.getName());
                    teamInfo.put("userCount", team.getCollaborators().size());
                    return teamInfo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getCollaboratorUsernamesByTeam(Long teamId) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        return team.getCollaborators().stream()
                .map(collaborator -> {
                    Map<String, Object> collaboratorInfo = new HashMap<>();
                    collaboratorInfo.put("id", collaborator.getId());
                    collaboratorInfo.put("username", collaborator.getUsername());
                    return collaboratorInfo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getChefInfoByTeam(Long teamId) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with ID: " + teamId));

        User chef = team.getChef();
        Map<String, Object> chefInfo = new HashMap<>();
        chefInfo.put("chefId", chef.getId());
        chefInfo.put("chefUsername", chef.getUsername());

        return chefInfo;
    }

    @Override
    public Map<String, Object> getTeamByTeamManager(Long chefId) {
        User chef = userRepo.findById(Math.toIntExact(chefId))
                .orElseThrow(() -> new RuntimeException("TeamManager not found"));
        List<Team> teams = teamRepo.findByChef(chef);
        if (teams.isEmpty()) {
            throw new RuntimeException("Chef does not manage any team");
        }
        Team team = teams.get(0);

        Map<String, Object> teamData = Map.of(
                "teamId", team.getId(),
                "teamName", team.getName(),
                "collaborators", team.getCollaborators().stream()
                        .map(collaborator -> Map.of(
                                "id", collaborator.getId(),
                                "name", collaborator.getUsername()
                        ))
                        .collect(Collectors.toList())
        );

        return teamData;
    }



    @Override
    public Map<String, Object> getTeamByCollaboratorId(Long collaboratorId) {
        User collaborator = userRepo.findById(Math.toIntExact(collaboratorId))
                .orElseThrow(() -> new RuntimeException("Collaborator not found with ID: " + collaboratorId));
        Optional<Team> optionalTeam = teamRepo.findByCollaboratorsContaining(collaborator);
        if (optionalTeam.isEmpty()) {
            Map<String, Object> emptyResponse = new HashMap<>();
            emptyResponse.put("message", "Collaborator does not belong to any team");
            return emptyResponse;
        }
        Team team = optionalTeam.get();
        Map<String, Object> teamInfo = new HashMap<>();
        teamInfo.put("teamId", team.getId());
        teamInfo.put("teamName", team.getName());
        teamInfo.put("chefUsername", team.getChef().getUsername());
        return teamInfo;
    }

    @Override
    @Transactional
    public void leaveTeam(Long teamId, Long collaboratorId) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + teamId));
        Integer collaboratorIdAsInt = collaboratorId.intValue();
        User collaborator = userRepo.findById(collaboratorIdAsInt)
                .orElseThrow(() -> new EntityNotFoundException("Collaborator not found with id: " + collaboratorId));
        if (!team.getCollaborators().contains(collaborator)) {
            throw new RuntimeException("Collaborator is not part of the team");
        }
        team.getCollaborators().remove(collaborator);
        teamRepo.save(team);
    }

    @Override
    public Map<String, Object> getTeamInfoByUserId(Long userId) {
        User user = userRepo.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        List<Team> teamsAsChef = teamRepo.findByChef(user);
        if (!teamsAsChef.isEmpty()) {
            Team team = teamsAsChef.get(0);
            return Map.of(
                    "teamId", team.getId(),
                    "teamName", team.getName(),
                    "role", "CHEF"
            );
        }
        Optional<Team> teamAsCollaborator = teamRepo.findByCollaboratorsContaining(user);
        if (teamAsCollaborator.isPresent()) {
            Team team = teamAsCollaborator.get();
            return Map.of(
                    "teamId", team.getId(),
                    "teamName", team.getName(),
                    "role", "COLLABORATOR"
            );
        }
        return Map.of(
                "message", "User does not belong to any team",
                "userId", userId
        );
    }
}
