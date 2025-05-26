package com.example.PortailRH.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "formations")
public class Formation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    public String getUsername() {
        return (user != null) ? user.getUsername() : "N/A";
    }
    @OneToMany(mappedBy = "formation" ,fetch = FetchType.EAGER)
    private List<Certificat> certificats;
    @OneToMany(mappedBy = "formation",fetch = FetchType.EAGER)
    private List<Competance> competances;



}
