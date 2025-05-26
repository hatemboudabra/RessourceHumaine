package com.example.PortailRH.repository;

import com.example.PortailRH.entity.Projet;
import com.example.PortailRH.entity.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepo extends JpaRepository<Projet,Long> {
    List<Projet> findByChefId(Long chefId);
}
