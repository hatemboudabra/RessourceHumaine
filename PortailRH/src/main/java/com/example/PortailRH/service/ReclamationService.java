package com.example.PortailRH.service;

import com.example.PortailRH.entity.Reclamation;
import com.example.PortailRH.entity.dto.ReclamationDTO;

import java.util.List;
import java.util.Optional;

public interface ReclamationService {
    public Reclamation addReclamation(ReclamationDTO reclamationDTO);
    public void DeleteReclamation(Long id);
    public List<Reclamation> getallReclamation();
    public Optional<Reclamation> getById(Long id);
    List<Reclamation> findByUserId(Long userId);
}
