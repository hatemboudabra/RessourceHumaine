package com.example.PortailRH.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReclamationDTO {
    private String title;
    private String description;
    private Long userId;
}
