package com.example.PortailRH.repository;

import com.example.PortailRH.entity.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormationRepo extends JpaRepository<Formation,Long> {
    List<Formation> findByUserId(Long userId);

}
