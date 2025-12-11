package com.example.keejobstore.entity;

import com.example.keejobstore.converter.CVConverter;
import com.example.keejobstore.converter.PriceConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class CoachingEmploi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    long id;

    private String name;
    private String titre;
    private String sousTitre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "JSON")
    @Convert(converter = CVConverter.class)
    private List<CoachingSection> sections = new ArrayList<>();

    private String image;

    @ManyToMany
    @JoinTable(
            name = "coaching_partenaire",
            joinColumns = @JoinColumn(name = "coaching_id"),
            inverseJoinColumns = @JoinColumn(name = "partenaire_id")
    )
    private List<Partenaire> coachingPartenaires = new ArrayList<>();


    @Column(columnDefinition = "JSON")
    @Convert(converter = PriceConverter.class)
    private List<PriceSection> priceSections = new ArrayList<>();

}

