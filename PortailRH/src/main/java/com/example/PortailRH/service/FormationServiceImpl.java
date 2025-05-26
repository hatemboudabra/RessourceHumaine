package com.example.PortailRH.service;

import com.example.PortailRH.entity.Formation;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.FormationDTO;
import com.example.PortailRH.repository.FormationRepo;
import com.example.PortailRH.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FormationServiceImpl implements FormationService{
    private final FormationRepo formationRepo;
    private final UserRepo userRepo;

    public FormationServiceImpl(FormationRepo formationRepo, UserRepo userRepo) {
        this.formationRepo = formationRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Formation addFormation(FormationDTO formationDTO) {
        Formation formation = new Formation();
        formation.setNom(formationDTO.getNom());
        formation.setDescription(formationDTO.getDescription());
        User user = userRepo.findById(Math.toIntExact(formationDTO.getUserId())).get();
        formation.setUser(user);

        return formationRepo.save(formation);
    }

    @Override
    public Formation updateFormation(Long id, FormationDTO formationDTO) {
        Formation formation = formationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation not found with id: " + id));
        formation.setNom(formationDTO.getNom());
        formation.setDescription(formationDTO.getDescription());
        if (formationDTO.getUserId() != null) {
            User user = userRepo.findById(Math.toIntExact(formationDTO.getUserId()))
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + formationDTO.getUserId()));
            formation.setUser(user);
        }
        return formationRepo.save(formation);
    }

    @Override
    public List<Formation> getall() {
        return formationRepo.findAll();
    }

    @Override
    public List<String> getAllFormationsWithUsernames() {
        List<Formation> formations = formationRepo.findAll();

        return formations.stream()
                .map(formation -> {
                    String nomFormation = formation.getNom();
                    User user = formation.getUser();
                    String username = (user != null) ? user.getUsername() : "N/A";
                    return "Formation: " + nomFormation + ", Utilisateur: " + username;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Formation> getFormationByID(Long id) {
        return formationRepo.findById(id);
    }

    @Override
    public List<Formation> findByUserId(Long userId) {
        return formationRepo.findByUserId(userId);
    }


    }

