package com.example.PortailRH.repository;

import com.example.PortailRH.entity.Evaluation;
import com.example.PortailRH.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepo extends JpaRepository<Evaluation,Long> {
    List<Evaluation> findByUser(User user);
    @Query("SELECT e.user.username, AVG(e.note) FROM Evaluation e GROUP BY e.user.username")
    List<Object[]> findAverageNoteForAllUsers();


}
