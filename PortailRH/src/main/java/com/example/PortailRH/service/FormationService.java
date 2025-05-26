package com.example.PortailRH.service;

import com.example.PortailRH.entity.Formation;
import com.example.PortailRH.entity.dto.FormationDTO;

import javax.crypto.spec.OAEPParameterSpec;
import java.util.List;
import java.util.Optional;

public interface FormationService {
    public Formation addFormation(FormationDTO formationDTO);
    public Formation updateFormation(Long id,FormationDTO formationDTO);
    public List<Formation> getall();
    public Optional<Formation> getFormationByID(Long id);
    List<Formation> findByUserId(Long userId);
    public List<String> getAllFormationsWithUsernames();
}
