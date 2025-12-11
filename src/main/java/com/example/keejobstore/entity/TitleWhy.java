package com.example.keejobstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TitleWhy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "formateur")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // ✅ Permet la désérialisation mais pas la sérialisation
    private Formateur formateur;
}