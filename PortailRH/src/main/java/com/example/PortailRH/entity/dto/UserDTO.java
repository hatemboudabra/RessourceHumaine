package com.example.PortailRH.entity.dto;

import com.example.PortailRH.entity.enummerations.RoleName;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @Column(unique = true)
    private String username;
    private String password;
    @Email
    //@Column(unique = true, nullable = false)
    private String email;
    private String post;

    private List<RoleName> roles;
    //
    private String sexe;
    private String language = "FR";
    private LocalDate dateOfBirth;
    private String nationality;
    private String phone1;
    private String address;
    private String civilStatus;
    private String city;
    private String postalCode;
    private String country;
}
