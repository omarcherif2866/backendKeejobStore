package com.example.keejobstore.repository;

import com.example.keejobstore.entity.CVandLetter;
import com.example.keejobstore.entity.CategoryCV;
import com.example.keejobstore.entity.CategoryCoaching;
import com.example.keejobstore.entity.CoachingEmploi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoachingRepository extends JpaRepository<CoachingEmploi,Long > {
    List<CoachingEmploi> findByCategoryCoaching(CategoryCoaching category);

}
