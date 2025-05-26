package com.example.PortailRH.repository;

import com.example.PortailRH.entity.Competance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetanceRepo extends JpaRepository<Competance,Long> {
}
