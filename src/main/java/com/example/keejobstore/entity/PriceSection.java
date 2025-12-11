package com.example.keejobstore.entity;

import com.example.keejobstore.converter.DetailObjectListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceSection {
    private String title;
    private String subtitle;
    private float price;

    @Column(columnDefinition = "JSON")
    @Convert(converter = DetailObjectListConverter.class)
    List<DetailObject> details = new ArrayList<>();
}


