package com.example.PortailRH.service;

import com.example.PortailRH.entity.Projet;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.ProjectDTO;
import com.example.PortailRH.repository.ProjectRepo;
import com.example.PortailRH.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjetService{
    private final ProjectRepo projectRepo;
    private final UserRepo userRepo;

    public ProjectServiceImpl(ProjectRepo projectRepo, UserRepo userRepo) {
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<Projet> getAllProjet() {
        return projectRepo.findAll();
    }

    @Override
    public Projet getProjetById(Long id) {
        return projectRepo.findById(id).get();
    }

    @Override
    public Projet addProjet(ProjectDTO projectDTO) {
        Projet projet = new Projet();
        projet.setName(projectDTO.getName());
        projet.setDescription(projectDTO.getDescription());
        User user = userRepo.findById(Math.toIntExact(projectDTO.getChefId())).get();
        projet.setChef(user);
        return projectRepo.save(projet);

    }

    @Override
    public List<Projet> findByChefId(Long chefId) {
        return projectRepo.findByChefId(chefId);
    }

    @Override
    public void deleteProjetById(Long id) {
        projectRepo.deleteById(id);
    }

    @Override
    public Projet updateProjet(Long id, ProjectDTO projectDTO) {
        Projet projet = projectRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projet.setName(projectDTO.getName());
        projet.setDescription(projectDTO.getDescription());
        if (projectDTO.getChefId() != null) {
            User user = userRepo.findById(projectDTO.getChefId().intValue())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            projet.setChef(user);
        }
        return projectRepo.save(projet);
    }


}
