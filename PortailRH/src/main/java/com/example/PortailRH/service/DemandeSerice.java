package com.example.PortailRH.service;

import com.example.PortailRH.entity.Demande;
import com.example.PortailRH.entity.dto.DemandeDTO;
import com.example.PortailRH.entity.enummerations.Status;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DemandeSerice {
    public Demande addDemande(DemandeDTO demandeDTO);
    public Demande UpdateDemande (Long id , DemandeDTO demandeDTO);
    public List<Demande> getallDemande();
    public Optional<Demande> getById(Long id);
    public Demande changeStatus(Long id, Status newStatus);
    public Demande changeStatusForLoanOrAdvance(Long id, Status newStatus);
    public Demande changeStatusForDocumentOrTrainingOrLeave(Long id, Status newStatus);
    public List<Demande> getLoanOrAdvanceRequests();
    public List<Demande> getDocumentTrainingOrLeaveRequests();
    public List<Demande> getDemandesByUserId(Long userId);
 //   public List<Demande> getDemandesEquipeChef(Long chefId);
 Map<Status, Long> getDemandesCountByStatusForDocumentTrainingOrLeave();
 Map<Status,Long> getDemandesCountByStatusForLoanOrAdvanceRequest();
    public void DeleteDemande(Long id);
    // calender
    Map<String, Long> getDemandesCountByMonth();

}
