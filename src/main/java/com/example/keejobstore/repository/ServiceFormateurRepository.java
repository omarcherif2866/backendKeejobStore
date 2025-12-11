package com.example.keejobstore.repository;

import com.example.keejobstore.entity.ServiceFromateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceFormateurRepository extends JpaRepository<ServiceFromateur,Long > {
    List<ServiceFromateur> findByFormateurId(Long formateurId);
}
