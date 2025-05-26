package com.example.PortailRH.controller;

import com.example.PortailRH.entity.Offers;
import com.example.PortailRH.entity.dto.OffersDTO;
import com.example.PortailRH.service.OffersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class OffersController {
    private final OffersService offersService;

    public OffersController(OffersService offersService) {
        this.offersService = offersService;
    }
    @PostMapping("/addOffers")
    public ResponseEntity<Offers> addOffer(@RequestBody OffersDTO offersDTO) {
        Offers offer = offersService.addOffers(offersDTO);
        return ResponseEntity.ok(offer);
    }

    @GetMapping("/alloffers")
    public ResponseEntity<List<Offers>> getAllOffers() {
        List<Offers> offersList = offersService.getallOffers();
        return ResponseEntity.ok(offersList);
    }

    @GetMapping("Offers/{id}")
    public ResponseEntity<Offers> getOfferById(@PathVariable Long id) {
        Optional<Offers> offer = offersService.getOffersByid(id);
        return offer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("DeleteOffers/{id}")
    public ResponseEntity<Void> deleteOffers(@PathVariable Long id) {
        offersService.deleteOffersById(id);
        return ResponseEntity.noContent().build();
    }
}
