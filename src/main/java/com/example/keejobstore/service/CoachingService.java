package com.example.keejobstore.service;

import com.example.keejobstore.entity.CoachingEmploi;

import java.util.List;

public interface CoachingService {
    CoachingEmploi addCoachingEmploi(CoachingEmploi CoachingEmplois);
    void deleteCoachingEmploiEntityById(Long id);
    CoachingEmploi getCoachingEmploiById(Long id);
    public List<CoachingEmploi> getAllCoachingEmplois();
    CoachingEmploi updateCoachingEmploi(Long id, CoachingEmploi CoachingEmploi);
}
