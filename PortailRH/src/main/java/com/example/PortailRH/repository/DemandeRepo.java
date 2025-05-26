package com.example.PortailRH.repository;

import com.example.PortailRH.entity.Demande;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.enummerations.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeRepo extends JpaRepository<Demande,Long> {
    List<Demande> findByTypeIn(List<Type> types);
    List<Demande> findByUserId(Long userId);

   // List<Demande> findByUserInAndTypeIn(List<User> teamMembers, List<Type> demandeTypes);

    @Query("SELECT d.status, COUNT(d) FROM Demande d WHERE d.type IN (:types) GROUP BY d.status")
    List<Object[]> countDemandesByStatusForTypes(@Param("types") List<Type> types);



}
