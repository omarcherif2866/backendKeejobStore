package com.example.keejobstore.entity;

import com.example.keejobstore.converter.DetailObjectListConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class SousFormationkeejob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String title;
    private String image;

    @ManyToMany
    @JoinTable(
            name = "sousFormation_partenaire",
            joinColumns = @JoinColumn(name = "sous_formation_id"), // ✅ Changé
            inverseJoinColumns = @JoinColumn(name = "partenaire_id")
    )

    private List<Partenaire> sousFormationPartenaires = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "formation_id")
    @JsonIgnore    // 👉 Ajoutez ceci
    private FormationKeejob formation;

    @ManyToMany
    @JoinTable(
            name = "sousFormation_logiciel",
            joinColumns = @JoinColumn(name = "sous_formation_id"), // côté propriétaire
            inverseJoinColumns = @JoinColumn(name = "logiciel_id") // côté inverse
    )
    @JsonIgnore    // 👉 Ajoutez ceci
    private List<Logiciel> sousFormationLogiciel = new ArrayList<>();

    private String titleLogiciel;

    @Column(columnDefinition = "JSON")
    @Convert(converter = DetailObjectListConverter.class)
    private List<DetailObject> details = new ArrayList<>();


}
