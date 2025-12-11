package com.example.keejobstore.repository;

import com.example.keejobstore.entity.SousFormationkeejob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SousFormationKeejobRepository extends JpaRepository<SousFormationkeejob, Long> {
    List<SousFormationkeejob> findByFormationId(Long formationId);

}
