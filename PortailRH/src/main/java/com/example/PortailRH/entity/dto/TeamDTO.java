package com.example.PortailRH.entity.dto;

import com.example.PortailRH.entity.enummerations.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
    private String name;
    private List<RoleName> roles;
}
