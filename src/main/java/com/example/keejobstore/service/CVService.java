package com.example.keejobstore.service;

import com.example.keejobstore.entity.*;

import java.util.List;
import java.util.Map;

public interface CVService {
    CVandLetter addCVandLetter(CVandLetter CVandLetters);
    void deleteCVandLetterEntityById(Long id);
    CVandLetter getCVandLetterById(Long id);
    public List<CVandLetter> getAllCVandLetters();
    CVandLetter updateCVandLetter(Long id, CVandLetter CVandLetter);
    List<CVandLetter> findByCategoryCV(CategoryCV category);

}
