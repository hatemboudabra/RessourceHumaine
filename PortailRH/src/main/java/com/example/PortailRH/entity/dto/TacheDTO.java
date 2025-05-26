package com.example.PortailRH.entity.dto;

import com.example.PortailRH.entity.enummerations.ComplexiteTache;
import com.example.PortailRH.entity.enummerations.StatusTache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TacheDTO {
    private String title;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private StatusTache statusTache;
    private ComplexiteTache complexite;
    private Long userId;
    private Long projectId;
    private String projectName;
}
