package com.example.PortailRH.service;

import com.example.PortailRH.entity.Offers;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.OffersDTO;
import com.example.PortailRH.repository.OffersRepo;
import com.example.PortailRH.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
public class OffersServiceImpl implements OffersService{
    private final OffersRepo offersRepo;
    private final UserRepo userRepo;

    public OffersServiceImpl(OffersRepo offersRepo, UserRepo userRepo) {
        this.offersRepo = offersRepo;
        this.userRepo = userRepo;
    }
    @Override
    public Offers addOffers(OffersDTO offersDTO) {
        Offers offers = new Offers();
        offers.setTitle(offersDTO.getTitle());
        offers.setDescription(offersDTO.getDescription());
        offers.setSalary(offersDTO.getSalary());
        offers.setContractType(offersDTO.getContractType());
        offers.setPublicationDate(offersDTO.getPublicationDate());
        offers.setExpirationDate(offersDTO.getExpirationDate());
        offers.setEducationLevel(offersDTO.getEducationLevel());
        offers.setExperience(offersDTO.getExperience());
        User user = userRepo.findById(Math.toIntExact(offersDTO.getCreatedById())).get();
        offers.setUser(user);
        return offersRepo.save(offers);
    }

    @Override
    public List<Offers> getallOffers() {
        return offersRepo.findAll();
    }

    @Override
    public Optional<Offers> getOffersByid(Long id) {
        return offersRepo.findById(id);
    }

    @Override
    public void deleteOffersById(Long id) {
        offersRepo.deleteById(id);
    }

    @Override
    public Offers updateOffersById(Long id, OffersDTO offersDTO) {
        return null;
    }
}
