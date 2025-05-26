package com.example.PortailRH.repository;

import com.example.PortailRH.entity.Formation;
import com.example.PortailRH.entity.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReclamationRepo extends JpaRepository<Reclamation,Long> {
    List<Reclamation> findByUserId(Long userId);
}
