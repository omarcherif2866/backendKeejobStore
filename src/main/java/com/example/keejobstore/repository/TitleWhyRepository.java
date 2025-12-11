package com.example.keejobstore.repository;

import com.example.keejobstore.entity.TitleWhy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TitleWhyRepository extends JpaRepository<TitleWhy,Long > {
    List<TitleWhy> findByFormateurId(Long formateurId);
}
