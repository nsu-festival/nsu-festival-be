package com.example.nsu_festival.domain.booth.controller;

import com.example.nsu_festival.domain.booth.dto.BoothDto;
import com.example.nsu_festival.domain.booth.service.BoothService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoothController {
    private final BoothService boothService;

    @GetMapping("/booth")
    public ResponseEntity<List<BoothDto>> getAllBooths(){
        try{
            List<BoothDto> booths = boothService.getAllBooths();
            return ResponseEntity.ok(booths);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
