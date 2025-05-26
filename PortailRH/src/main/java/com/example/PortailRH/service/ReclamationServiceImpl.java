package com.example.PortailRH.service;

import com.example.PortailRH.entity.Reclamation;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.ReclamationDTO;
import com.example.PortailRH.repository.ReclamationRepo;
import com.example.PortailRH.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ReclamationServiceImpl implements ReclamationService{
    private final ReclamationRepo reclamationRepo;
    private final UserRepo userRepo;

    public ReclamationServiceImpl(ReclamationRepo reclamationRepo, UserRepo userRepo) {
        this.reclamationRepo = reclamationRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Reclamation addReclamation(ReclamationDTO reclamationDTO) {
        Reclamation reclamation = new Reclamation();
        reclamation.setTitle(reclamationDTO.getTitle());
        reclamation.setDescription(reclamationDTO.getDescription());
        User user = userRepo.findById(Math.toIntExact(reclamationDTO.getUserId())).get();
        reclamation.setUser(user);
        return reclamationRepo.save(reclamation);
    }

    @Override
    public void DeleteReclamation(Long id) {
        reclamationRepo.deleteById(id);

    }

    @Override
    public List<Reclamation> getallReclamation() {
        return reclamationRepo.findAll();
    }

    @Override
    public Optional<Reclamation> getById(Long id) {
        return reclamationRepo.findById(id);
    }

    @Override
    public List<Reclamation> findByUserId(Long userId) {
        return reclamationRepo.findByUserId(userId);
    }
}
