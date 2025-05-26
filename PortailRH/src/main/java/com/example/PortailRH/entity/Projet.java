package com.example.PortailRH.entity;

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
@Table(name = "projets")
public class Projet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
//    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Tache> taches;
//    @ManyToOne
//    @JoinColumn(name = "chef_id", nullable = false)
//    private User chef;
    @ManyToOne
    @JoinColumn(name = "chef_id", nullable = false)
    private User chef;
}
