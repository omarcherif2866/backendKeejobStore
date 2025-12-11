package com.example.keejobstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EvaluationCatalogue {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String image;
    @ManyToOne
    @JoinColumn(name = "evaluation_id", referencedColumnName = "id")
    @JsonIgnore // <-- empêche la récursion infinie
    private Evaluation evaluation;
}
