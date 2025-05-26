package com.example.PortailRH.service;

import com.example.PortailRH.entity.Candidat;
import com.example.PortailRH.entity.dto.CandidarDTO;

import java.util.List;

public interface CandidatService {
    public Candidat postuler(CandidarDTO candidarDTO);
    public List<Candidat> getAllCandidatures() ;
    public List<String> getAllCandidateEmails();
    void deltetCandidatById(Long id);

    public void sendInterviewEmail(Long candidatId, String interviewDate, String interviewTime, String meetingLink);
}