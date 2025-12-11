package com.example.keejobstore.repository;

import com.example.keejobstore.entity.CVandLetter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CVRepository extends JpaRepository<CVandLetter,Long > {
}
