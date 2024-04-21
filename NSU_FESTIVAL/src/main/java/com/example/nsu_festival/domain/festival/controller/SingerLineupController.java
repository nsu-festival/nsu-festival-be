package com.example.nsu_festival.domain.festival.controller;

import com.example.nsu_festival.domain.festival.dto.FestivalProgramResponseDto;
import com.example.nsu_festival.domain.festival.dto.SingerLineupResponseDto;
import com.example.nsu_festival.domain.festival.service.FestivalProgramService;
import com.example.nsu_festival.domain.festival.service.SingerLineupService;
import com.example.nsu_festival.global.etc.StatusResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@Controller
@Slf4j
@AllArgsConstructor
public class SingerLineupController {
    private final SingerLineupService singerLineupService;
    @GetMapping("/singerlineups/days/{dDay}")
    public ResponseEntity<StatusResponseDto> findSingerLineupList(@PathVariable LocalDate dDay){
        try{
            if(singerLineupService.isCorrectDate(dDay)) {
                List<SingerLineupResponseDto> singerLineupResponseDtoList = singerLineupService.findSingerLineupList(dDay);
                return ResponseEntity.ok().body(StatusResponseDto.success(singerLineupResponseDtoList));
            }
            return ResponseEntity.status(400).body(StatusResponseDto.addStatus(400));
        } catch (RuntimeException e){
            log.error(e.getMessage());
            return ResponseEntity.status(500).body(StatusResponseDto.addStatus(500));
        }
    }
}
