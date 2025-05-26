package com.example.PortailRH.repository;

import com.example.PortailRH.entity.Team;
import com.example.PortailRH.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepo extends JpaRepository<Team,Long> {
    List<Team> findByChef(User chef);
    List<Team> findByChef_Id(Long chefId);
    Optional<Team> findByCollaboratorsContaining(User collaborator);


}
