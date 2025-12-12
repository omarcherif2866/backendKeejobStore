package com.example.keejobstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FormationKeejob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;
    private String logo;
    @Column(columnDefinition = "TEXT")
    private String description;

    private String title;
    private String image;

    @ManyToMany
    @JoinTable(
            name = "formation_partenaire",
            joinColumns = @JoinColumn(name = "formation_id"),
            inverseJoinColumns = @JoinColumn(name = "partenaire_id")
    )
    private List<Partenaire> partenaires = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private CategoryFormationKeejob categoryFormationKeejob ;

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore   // éviter la boucle formation → sousFormation → formation
    private List<SousFormationkeejob> sousFormations = new ArrayList<>();
}
