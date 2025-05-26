package com.example.PortailRH.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffersDTO {
    private String title;
    private String description;
    private Double salary;
    private String contractType;
    private Date publicationDate;
    private Date expirationDate;
    private Long createdById;
    private String educationLevel;
    private int experience;
}
