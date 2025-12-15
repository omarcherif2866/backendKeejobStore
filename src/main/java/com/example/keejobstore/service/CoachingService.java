package com.example.keejobstore.service;

import com.example.keejobstore.entity.CVandLetter;
import com.example.keejobstore.entity.CategoryCV;
import com.example.keejobstore.entity.CategoryCoaching;
import com.example.keejobstore.entity.CoachingEmploi;

import java.util.List;

public interface CoachingService {
    CoachingEmploi addCoachingEmploi(CoachingEmploi CoachingEmplois);
    void deleteCoachingEmploiEntityById(Long id);
    CoachingEmploi getCoachingEmploiById(Long id);
    public List<CoachingEmploi> getAllCoachingEmplois();
    CoachingEmploi updateCoachingEmploi(Long id, CoachingEmploi CoachingEmploi);
    List<CoachingEmploi> findByCategoryCoaching(CategoryCoaching category);

}
