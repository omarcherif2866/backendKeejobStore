package com.example.keejobstore.entity;

import com.example.keejobstore.converter.CVConverter;
import com.example.keejobstore.converter.EvaluationSectionListConverter;
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
public class CVandLetter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    long id;

    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "JSON")
    @Convert(converter = CVConverter.class)
    private List<CVandLetterSection> sections = new ArrayList<>();

    private String image;

    @ManyToMany
    @JoinTable(
            name = "cv_partenaire",
            joinColumns = @JoinColumn(name = "cv_id"),
            inverseJoinColumns = @JoinColumn(name = "partenaire_id")
    )
    private List<Partenaire> cvPartenaires = new ArrayList<>();


    @Column(columnDefinition = "JSON")
    @Convert(converter = PriceConverter.class)
    private List<PriceSection> priceSections = new ArrayList<>();

}
