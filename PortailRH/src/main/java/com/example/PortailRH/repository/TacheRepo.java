package com.example.PortailRH.repository;

import com.example.PortailRH.entity.Tache;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.enummerations.StatusTache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacheRepo extends JpaRepository<Tache,Long> {
    List<Tache> findByUser(User user);
    List<Tache> findAllByUserIn(List<User> users);
    List<Tache> findAllByCreatedBy(User createdBy);
    List<Tache> findAllByCreatedByAndUserAndStatusTache(User createdBy, User user, StatusTache statusTache);
    List<Tache> findAllByUser(User user);
}
