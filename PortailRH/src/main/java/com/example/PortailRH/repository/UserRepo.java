package com.example.PortailRH.repository;

import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.UserDTO;
import com.example.PortailRH.entity.enummerations.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<User> findByRolesIn(List<RoleName> roles);
    long countByRolesContaining(RoleName role);
    Optional<User> findByResetToken(String resetToken);
    List<User> findAllByUsernameIn(List<String> usernames);
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.formations WHERE u.id = :id")
    Optional<User> findByIdWithFormations(@Param("id") Long id);

    default User findUserWithRelations(Long id) {
        User user = findByIdWithFormations(id).orElseThrow(() -> new RuntimeException("User not found"));

        // Initialisation manuelle des relations
        if (user.getFormations() != null) {
            user.getFormations().forEach(formation -> {
                // Force le chargement des compétences et certificats
                if (formation.getCompetances() != null) {
                    formation.getCompetances().size();
                }
                if (formation.getCertificats() != null) {
                    formation.getCertificats().size();
                }
            });
        }

        return user;
    }
}

