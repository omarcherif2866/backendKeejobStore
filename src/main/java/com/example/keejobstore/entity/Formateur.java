package com.example.keejobstore.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Formateur {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String lastName;
    private String firstName;
    private String phone;
    private String address;
    private String university;
    private String experience;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String poste;
    private String image;

    @OneToMany(mappedBy = "formateur", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ServiceFromateur> servicesFormateurs = new ArrayList<>();
    @OneToMany(mappedBy = "formateur", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<TitleWhy> titleWhyList = new ArrayList<>();

    @OneToMany(mappedBy = "formateur", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<FormationFormateur> formationFormateurs = new ArrayList<>();
}
