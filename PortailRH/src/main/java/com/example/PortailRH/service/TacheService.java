package com.example.PortailRH.service;

import com.example.PortailRH.entity.Tache;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.TacheDTO;
import com.example.PortailRH.entity.enummerations.StatusTache;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TacheService {
  public Tache addTacheByChef(Long chefId, TacheDTO tacheDTO);
  public List<Tache> getallTache();
  public Optional<Tache> getTacheById(Long id);
    public Tache updateStatus(Long tacheId, StatusTache newStatus);
//    public Tache assignTache(Long tacheId, Long userId);
  public Tache assignTacheToCollaborator(Long tacheId, Long chefId, Long collaboratorId);
  public List<Tache> gettacheByChef(Long chefId);
  List<Tache> getAssignedTachesByChef(Long chefId, Long collaboratorId);
  public String getAssignedCollaboratorUsername(Long tacheId);
  public Map<StatusTache, Long> countTachesByStatusAndChef(Long chefId) ;
  Map<String, Long> countTachesAssignedByChefToCollaborators(Long chefId);
  public List<Tache> getTachesByCollaboratorId(Long collaboratorId);
  public void DeleteTache(Long id);
}
