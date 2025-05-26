package com.example.PortailRH.entity;

import com.example.PortailRH.entity.enummerations.RoleName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String username;

    private String password;
    //@Column(unique = true, nullable = false)
    @Email
    private String email;
    private String post;
//    @Column(name = "image")
//    private String image;

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
    //
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = RoleName.class)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Fetch(FetchMode.JOIN)
    private List<RoleName> roles = new ArrayList<>();
//    @OneToMany(mappedBy = "chef")
//    private List<Projet> projects = new ArrayList<>();


    @OneToMany(mappedBy = "user")
    private List<Demande> demandes;

    @OneToMany(mappedBy = "user")
    private List<Tache> taches;

    @OneToMany(mappedBy = "user")
    private List<Formation> formations;


    @OneToMany(mappedBy = "user")
    private List<Reclamation> reclamations;
    @OneToMany(mappedBy = "user")
    private List<Evaluation> evaluations;

//    @ManyToOne
//    @JoinColumn(name = "team_id", nullable = false)
//    private Team team;
    @Column
    private Double noteGlobale = 0.0;
    @Column(nullable = true)
    private String resetToken;

    @OneToMany(mappedBy = "user")
    private List<Offers> offers = new ArrayList<>();
}
