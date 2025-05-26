package com.example.PortailRH.repository;

import com.example.PortailRH.entity.Offers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OffersRepo extends JpaRepository<Offers,Long> {
}
