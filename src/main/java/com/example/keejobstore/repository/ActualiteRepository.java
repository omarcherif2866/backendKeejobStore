package com.example.keejobstore.repository;

import com.example.keejobstore.entity.Actualites;
import com.example.keejobstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActualiteRepository extends JpaRepository<Actualites,Long > {
}
