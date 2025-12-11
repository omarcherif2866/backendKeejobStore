package com.example.keejobstore.service;

import com.example.keejobstore.entity.Logiciel;
import com.example.keejobstore.entity.SousFormationkeejob;

import java.util.List;

public interface LogicielService {
    Logiciel addLogiciel(Logiciel Logiciels);
    void deleteLogicielEntityById(Long id);
    Logiciel getLogicielById(Long id);
    public List<Logiciel> getAllLogiciels();
    Logiciel updateLogiciel(Long id, Logiciel Logiciel);
    List<Logiciel> getLogicielBySousFormationKeejob(Long sousFormationId);

}
