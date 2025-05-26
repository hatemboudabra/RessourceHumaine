package com.example.PortailRH.entity.dto;

import com.example.PortailRH.entity.enummerations.Status;
import com.example.PortailRH.entity.enummerations.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeDTO {

    private String title; //tous type
    private String description; //tous type
    private Date date; //tous type
    private Status status; //tous type
    private Type type;
    private Long userId;
    private String documentType; // document
    private Double amount; // pret, avance
    private String loanType; // Pour pret
    private Long nbrejour; // congé
}
