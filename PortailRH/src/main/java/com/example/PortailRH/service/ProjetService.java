package com.example.PortailRH.service;

import com.example.PortailRH.entity.Projet;
import com.example.PortailRH.entity.dto.ProjectDTO;

import java.util.List;

public interface ProjetService {

    public List<Projet> getAllProjet();
    public Projet getProjetById(Long id);
    public Projet addProjet(ProjectDTO projectDTO);
    List<Projet> findByChefId(Long chefId);
    void deleteProjetById(Long id);
    public Projet updateProjet(Long id, ProjectDTO projectDTO);
}
