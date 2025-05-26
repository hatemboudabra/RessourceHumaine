package com.example.PortailRH.entity;

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
@Table(name = "canidats")
public class Candidat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String cv;
    private Date datepostule;
    @ManyToOne
    @JoinColumn(name = "offers_id")
    @JsonIgnore
    private Offers offers;
    public String getOfferTitle() {
        return (offers != null) ? offers.getTitle() : "N/A";
    }
    @PrePersist
    protected void onCreate() {
        this.datepostule = new Date();
    }
}
