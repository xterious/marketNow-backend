package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.dto.LiborRateRequest;
import com.marketview.Spring.MV.model.LiborRate;
import com.marketview.Spring.MV.repository.LiborRateRepository;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Data

@RestController
@RequestMapping("/api/admin")
public class AdminController {


    private final LiborRateRepository liborRateRepository;

    @PutMapping("/libor/update")
    public ResponseEntity<String> updateLibor(
            @RequestBody LiborRateRequest request) {

        LiborRate libor = liborRateRepository.findById("LIBOR")
                .orElse(new LiborRate());

        libor.setNormalRate(request.getNormalRate());
        libor.setSpecialRate(request.getSpecialRate());

        liborRateRepository.save(libor);
        return ResponseEntity.ok("LIBOR rates updated.");
    }
}