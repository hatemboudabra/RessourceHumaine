package com.example.PortailRH.service;

import com.example.PortailRH.entity.Offers;
import com.example.PortailRH.entity.dto.OffersDTO;

import java.util.List;
import java.util.Optional;

public interface OffersService {
    public Offers addOffers(OffersDTO offersDTO);
    public List<Offers> getallOffers();
    public Optional<Offers> getOffersByid(Long id);
    void deleteOffersById(Long id);
    Offers updateOffersById(Long id , OffersDTO offersDTO);
}
