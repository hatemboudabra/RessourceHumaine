package com.example.PortailRH.service;

import com.example.PortailRH.entity.Competance;
import com.example.PortailRH.entity.Formation;
import com.example.PortailRH.entity.dto.CompetanceDTO;
import com.example.PortailRH.repository.CompetanceRepo;
import com.example.PortailRH.repository.FormationRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompetanceServiceImpl implements CompetanceService{
    private final CompetanceRepo competanceRepo;
    private final FormationRepo formationRepo;

    public CompetanceServiceImpl(CompetanceRepo competanceRepo, FormationRepo formationRepo) {
        this.competanceRepo = competanceRepo;
        this.formationRepo = formationRepo;
    }

    @Override
    public Competance addComprtance(CompetanceDTO competanceDTO) {
        Competance competance = new Competance();
        competance.setNom(competanceDTO.getNom());
        competance.setNiveauC(competanceDTO.getNiveauC());
        Formation formation = formationRepo.findById(competanceDTO.getFormationId()).get();
        competance.setFormation(formation);
        return competanceRepo.save(competance);
    }


    @Override
    public Competance updateCompetance(Long id, CompetanceDTO competanceDTO) {
        Competance competance = competanceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Competance not found with id: " + id));
        competance.setNom(competanceDTO.getNom());
        competance.setNiveauC(competanceDTO.getNiveauC());
        if (competanceDTO.getFormationId() != null) {
            Formation formation = formationRepo.findById(competanceDTO.getFormationId())
                    .orElseThrow(() -> new RuntimeException("Formation not found with id: " + competanceDTO.getFormationId()));
            competance.setFormation(formation);
        }
        return competanceRepo.save(competance);
    }

    @Override
    public void deleteCompetance(Long id) {
            competanceRepo.deleteById(id);
    }

    @Override
    public List<Competance> getallCompetance() {

        return competanceRepo.findAll();
    }
}
