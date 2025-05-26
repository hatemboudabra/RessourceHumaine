package com.example.PortailRH.entity.dto;

import com.example.PortailRH.entity.enummerations.NiveauC;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetanceDTO {
    private String nom;
    private NiveauC niveauC;
    private Long formationId;
}
