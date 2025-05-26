package com.example.PortailRH.entity;

import com.example.PortailRH.entity.enummerations.ComplexiteTache;
import com.example.PortailRH.entity.enummerations.StatusTache;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "taches")
public class Tache implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusTache statusTache;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ComplexiteTache complexite;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private User createdBy;

   /* public String getUsername() {
        return (user != null) ? user.getUsername() : "N/A";
    }*/


   @ManyToOne
//    @JoinColumn(name = "projet_id", nullable = false)
   private Projet projet;

    public String getProjectName() {
        return (projet != null) ? projet.getName() : "N/A";
    }
}
