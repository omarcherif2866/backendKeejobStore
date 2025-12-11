package com.example.keejobstore.entity;

import com.example.keejobstore.configuration.ListToJsonConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class FormationFormateur {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private  String title;

    @Column(columnDefinition = "LONGTEXT")
    @Convert(converter = ListToJsonConverter.class)
    private List<String> description = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "formateur", referencedColumnName = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // ✅ Permet la désérialisation mais pas la sérialisation
    private Formateur formateur;
}
