package com.example.PortailRH.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidarDTO {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String cv;
    private Long offersId;
    private String offerTitle;

}
