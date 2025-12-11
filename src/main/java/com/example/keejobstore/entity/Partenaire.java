package com.example.keejobstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Partenaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String image;

    @ManyToMany(mappedBy = "partenaires")
    @JsonIgnore
    private List<FormationKeejob> formations = new ArrayList<>();

    @ManyToMany(mappedBy = "sousFormationPartenaires")
    @JsonIgnore
    private List<SousFormationkeejob> sousFormations = new ArrayList<>();

    @ManyToMany(mappedBy = "evaluationPartenaires")
    @JsonIgnore
    private List<Evaluation> evaluations = new ArrayList<>();

    @ManyToMany(mappedBy = "cvPartenaires")
    @JsonIgnore
    private List<CVandLetter> cv = new ArrayList<>();

    @ManyToMany(mappedBy = "coachingPartenaires")
    @JsonIgnore
    private List<CoachingEmploi> coaching = new ArrayList<>();
}

