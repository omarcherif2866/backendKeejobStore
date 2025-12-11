package com.example.keejobstore.repository;

import com.example.keejobstore.entity.Logiciel;
import com.example.keejobstore.entity.SousFormationkeejob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogicielRepository extends JpaRepository<Logiciel,Long > {
    List<Logiciel> findBySousFormationLogicielId(Long sousFormationId);

}
