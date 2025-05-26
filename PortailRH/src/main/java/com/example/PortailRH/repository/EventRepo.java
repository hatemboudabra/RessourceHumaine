package com.example.PortailRH.repository;

import com.example.PortailRH.entity.Event;
import com.example.PortailRH.entity.Reclamation;
import com.example.PortailRH.entity.dto.EventDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
    List<Event> findByUserId(Long userId);

}
