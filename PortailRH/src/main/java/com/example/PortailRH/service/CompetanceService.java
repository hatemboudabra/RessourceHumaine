package com.example.PortailRH.service;

import com.example.PortailRH.entity.Competance;
import com.example.PortailRH.entity.dto.CompetanceDTO;

import java.util.List;

public interface CompetanceService {
    public Competance addComprtance(CompetanceDTO competanceDTO);
    public Competance updateCompetance(Long id, CompetanceDTO competanceDTO);
    public void deleteCompetance(Long id);
    public List<Competance> getallCompetance();
}