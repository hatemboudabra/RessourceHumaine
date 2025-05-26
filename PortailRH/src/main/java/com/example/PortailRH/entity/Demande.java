package com.example.PortailRH.entity;

import com.example.PortailRH.entity.enummerations.Status;
import com.example.PortailRH.entity.enummerations.Type;
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
@Table(name = "demandes")
public class Demande implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //tous type
    private String title; //tous type
    private String description; //tous type
    private Date date;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status = Status.PENDING;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Type type;
    private String documentType; // document
    private Double amount; // pret, avance
    private String loanType; // Pour pret
    private Long nbrejour; // congé
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public String getUsername() {
        return (user != null) ? user.getUsername() : "N/A";
    }

}
