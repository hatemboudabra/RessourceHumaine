package com.example.PortailRH.repository;

import com.example.PortailRH.entity.Certificat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertifRepo extends JpaRepository<Certificat,Long> {
}
