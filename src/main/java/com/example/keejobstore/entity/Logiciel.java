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
public class Logiciel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //gerer par la base
    @Setter(AccessLevel.NONE)
    private long id;

    private String name;
    private String image;

    @ManyToMany(mappedBy = "sousFormationLogiciel")
    @JsonIgnore
    private List<SousFormationkeejob> sousFormationLogiciel = new ArrayList<>();
}
