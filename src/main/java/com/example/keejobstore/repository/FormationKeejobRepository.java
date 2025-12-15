package com.example.keejobstore.repository;

import com.example.keejobstore.entity.CategoryFormationKeejob;
import com.example.keejobstore.entity.FormationKeejob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormationKeejobRepository extends JpaRepository<FormationKeejob,Long > {
    List<FormationKeejob> findByCategoryFormationKeejob(CategoryFormationKeejob category);

}
