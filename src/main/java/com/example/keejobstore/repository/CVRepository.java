package com.example.keejobstore.repository;

import com.example.keejobstore.entity.CVandLetter;
import com.example.keejobstore.entity.CategoryCV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CVRepository extends JpaRepository<CVandLetter,Long > {
    List<CVandLetter> findByCategoryCV(CategoryCV category);
}
