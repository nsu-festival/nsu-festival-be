package com.example.nsu_festival.domain.booth.controller;

import com.example.nsu_festival.domain.booth.dto.BoothDetailDto;
import com.example.nsu_festival.domain.booth.dto.BoothDto;
import com.example.nsu_festival.domain.booth.dto.BoothResponseDto;
import com.example.nsu_festival.domain.booth.dto.BoothResponseStatus;
import com.example.nsu_festival.domain.booth.service.BoothService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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


    @GetMapping("/api/{boothId}")
    public ResponseEntity<BoothResponseDto<BoothDetailDto>> getBoothDetail(@PathVariable Long boothId) {
        try {
            BoothDetailDto boothDetailDto = boothService.getDetailBooth(boothId);
            BoothResponseDto<BoothDetailDto> responseDto = BoothResponseDto.<BoothDetailDto>builder()
                    .status(BoothResponseStatus.SUCCESS)
                    .message("성공")
                    .data(boothDetailDto)
                    .build();
            return ResponseEntity.ok().body(responseDto);
        } catch (Exception e) {
            BoothResponseDto<BoothDetailDto> responseDto = BoothResponseDto.<BoothDetailDto>builder()
                    .status(BoothResponseStatus.FAIL)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.ok().body(responseDto);
        }
    }

}
