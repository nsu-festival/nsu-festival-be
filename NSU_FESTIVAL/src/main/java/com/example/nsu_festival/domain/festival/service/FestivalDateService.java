package com.example.nsu_festival.domain.festival.service;

import com.example.nsu_festival.domain.festival.entity.FestivalDate;
import com.example.nsu_festival.domain.festival.repository.FestivalDateRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FestivalDateService implements InitializeDataService{
    private final FestivalProgramServiceImpl festivalProgramService;
    private final SingerLineupServiceImpl singerLineupService;
    private final FestivalDateRepository festivalDateRepository;
    @Override
    @PostConstruct
    public void initializeData() {
        FestivalDate festivalDate1 = new FestivalDate(1L, "2024-04-23");
        festivalDateRepository.save(festivalDate1);
        FestivalDate festivalDate2 = new FestivalDate(2L, "2024-04-24");
        festivalDateRepository.save(festivalDate2);
        FestivalDate festivalDate3 = new FestivalDate(3L, "2024-04-25");
        festivalDateRepository.save(festivalDate3);

        festivalProgramService.initializeData();
        singerLineupService.initializeData();
    }
}
