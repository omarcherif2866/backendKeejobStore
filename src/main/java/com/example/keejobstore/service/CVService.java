package com.example.keejobstore.service;

import com.example.keejobstore.entity.DetailObject;
import com.example.keejobstore.entity.CVandLetter;

import java.util.List;
import java.util.Map;

public interface CVService {
    CVandLetter addCVandLetter(CVandLetter CVandLetters);
    void deleteCVandLetterEntityById(Long id);
    CVandLetter getCVandLetterById(Long id);
    public List<CVandLetter> getAllCVandLetters();
    CVandLetter updateCVandLetter(Long id, CVandLetter CVandLetter);
}
