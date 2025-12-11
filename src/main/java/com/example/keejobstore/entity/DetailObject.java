package com.example.keejobstore.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DetailObject implements Serializable {
    String titre;
    String description;
    String icon;
    String category;  // Ajouter ce champ

}