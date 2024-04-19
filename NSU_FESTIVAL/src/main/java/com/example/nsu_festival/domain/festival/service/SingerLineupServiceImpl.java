package com.example.nsu_festival.domain.festival.service;

import com.example.nsu_festival.domain.festival.dto.FestivalProgramResponseDto;
import com.example.nsu_festival.domain.festival.dto.SingerLineupResponseDto;
import com.example.nsu_festival.domain.festival.entity.DDay;
import com.example.nsu_festival.domain.festival.entity.FestivalDate;
import com.example.nsu_festival.domain.festival.entity.FestivalProgram;
import com.example.nsu_festival.domain.festival.entity.SingerLineup;
import com.example.nsu_festival.domain.festival.repository.FestivalDateRepository;
import com.example.nsu_festival.domain.festival.repository.SingerLineupRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class SingerLineupServiceImpl implements SingerLineupService{
    private final FestivalDateRepository festivalDateRepository;
    private final SingerLineupRepository singerLineupRepository;

    /**
     *  가수 라인업 리스트를 찾고
     *  Dto로 변환된 리스트를 반환하는 메서드
     */
    @Override
    public List<SingerLineupResponseDto> findSingerLineupList(LocalDate dDay) {
        FestivalDate festivalDate = festivalDateRepository.findByDDay(dDay);
        List<SingerLineup> singerLineupList = singerLineupRepository.findAllByFestivalDate(festivalDate);
        List<SingerLineupResponseDto> singerLineupResponseDtoList = convertToDto(singerLineupList);
        return singerLineupResponseDtoList;
    }

    /**
     * 클라이언트로 전달할 리스트를
     * Dto로 변환하는 메서드
     */
    @Override
    public List<SingerLineupResponseDto> convertToDto(List<SingerLineup> singerLineupList) {
        List<SingerLineupResponseDto> singerLineupResponseDtoList = new ArrayList<>();
        for(SingerLineup singerLineup : singerLineupList){
            SingerLineupResponseDto singerLineupResponseDto = SingerLineupResponseDto.builder()
                    .singerLineupId(singerLineup.getSingerLineupId())
                    .singer(singerLineup.getSinger())
                    .countLike(singerLineup.getCountLike())
                    .startTime(singerLineup.getStartTime())
                    .endTime(singerLineup.getEndTime())
                    .build();

            singerLineupResponseDtoList.add(singerLineupResponseDto);
        }
        return singerLineupResponseDtoList;
    }

    /**
     *  클라이언트에서 요청한 날짜가
     *  올바른지 판별하는 메서드
     */
    @Override
    public boolean isCorrectDate(LocalDate dDay) {
        for (DDay day : DDay.values()) {
            if (day.getDate().equals(String.valueOf(dDay))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initializeData() {
        FestivalDate festivalDate1 = festivalDateRepository.findById(1L).orElseThrow(()->new RuntimeException("없는 축제날짜"));
        FestivalDate festivalDate2 = festivalDateRepository.findById(2L).orElseThrow(()->new RuntimeException("없는 축제날짜"));
        FestivalDate festivalDate3 = festivalDateRepository.findById(3L).orElseThrow(()->new RuntimeException("없는 축제날짜"));

        SingerLineup singerLineup1 = new SingerLineup(1L, "하이키", "20:00", "20:30", 0, festivalDate1);
        singerLineupRepository.save(singerLineup1);

        SingerLineup singerLineup2 = new SingerLineup(2L, "한요한", "20:30", "21:00", 0, festivalDate1);
        singerLineupRepository.save(singerLineup2);

        SingerLineup singerLineup3 = new SingerLineup(3L, "루시(LUCY)", "21:00", "21:30", 0, festivalDate1);
        singerLineupRepository.save(singerLineup3);

        SingerLineup singerLineup4 = new SingerLineup(4L, "하현상", "20:00", "20:30", 0, festivalDate2);
        singerLineupRepository.save(singerLineup4);

        SingerLineup singerLineup5 = new SingerLineup(5L, "QWER", "20:30", "21:00", 0, festivalDate2);
        singerLineupRepository.save(singerLineup5);

        SingerLineup singerLineup6 = new SingerLineup(6L, "10CM", "21:00", "21:30", 0, festivalDate2);
        singerLineupRepository.save(singerLineup6);

        SingerLineup singerLineup7 = new SingerLineup(7L, "최예나", "20:00", "20:30", 0, festivalDate3);
        singerLineupRepository.save(singerLineup7);

        SingerLineup singerLineup8 = new SingerLineup(8L, "비비", "20:30", "21:00", 0, festivalDate3);
        singerLineupRepository.save(singerLineup8);

        SingerLineup singerLineup9 = new SingerLineup(9L, "타이거JK & 윤미래", "21:00", "21:30", 0, festivalDate3);
        singerLineupRepository.save(singerLineup9);
    }
}
