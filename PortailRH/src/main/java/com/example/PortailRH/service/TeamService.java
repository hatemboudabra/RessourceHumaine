package com.example.PortailRH.service;

import com.example.PortailRH.entity.Team;
import com.example.PortailRH.entity.dto.TeamDTO;

import java.util.List;
import java.util.Map;

public interface TeamService {
    public Team createTeam(Long chefId, String teamName, List<Integer> collaboratorIds);
    public List<Team> getTeamsByChef(Long chefId);

    List<TeamDTO> getUserSummariesByTeam(Long teamId);
    List<Team> getallteam();
  //  public Team updateTeam(Long teamId, String newTeamName, List<Integer> collaboratorIdsToAdd, List<Integer> collaboratorIdsToRemove);
    public List<Map<String, Object>> getTeamsWithUserCount();
    public List<Map<String, Object>> getCollaboratorUsernamesByTeam(Long teamId);
    public Map<String, Object> getChefInfoByTeam(Long teamId);
    Map<String, Object> getTeamByTeamManager(Long chefId);

    public Team updateTeam(Long teamId, String newTeamName, List<String> collaboratorUsernamesToAdd, List<String> collaboratorUsernamesToRemove);
    //
    public Map<String, Object> getTeamByCollaboratorId(Long collaboratorId);
    public void leaveTeam(Long teamId, Long collaboratorId);
    //
    public Map<String, Object> getTeamInfoByUserId(Long userId);
}
