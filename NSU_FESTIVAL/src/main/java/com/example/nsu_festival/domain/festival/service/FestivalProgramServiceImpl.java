package com.example.nsu_festival.domain.festival.service;

import com.example.nsu_festival.domain.festival.dto.FestivalProgramResponseDto;
import com.example.nsu_festival.domain.festival.entity.DDay;
import com.example.nsu_festival.domain.festival.entity.FestivalDate;
import com.example.nsu_festival.domain.festival.entity.FestivalProgram;
import com.example.nsu_festival.domain.festival.repository.FestivalDateRepository;
import com.example.nsu_festival.domain.festival.repository.FestivalProgramRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
@AllArgsConstructor
public class FestivalProgramServiceImpl implements FestivalProgramService{
    private final FestivalProgramRepository festivalProgramRepository;
    private final FestivalDateRepository festivalDateRepository;

    /**
     *  축제 프로그램 리스트를 찾고
     *  Dto로 변환된 리스트를 반환하는 메서드
     */
    @Override
    public List<FestivalProgramResponseDto> findFestivalProgramList(LocalDate dDay) {
        FestivalDate festivalDate = festivalDateRepository.findByDDay(dDay);
        List<FestivalProgram> festivalProgramList = festivalProgramRepository.findAllByFestivalDate(festivalDate);
        List<FestivalProgramResponseDto> festivalProgramResponseDtoList = convertToDto(festivalProgramList);
        return festivalProgramResponseDtoList;
    }

    /**
     * 클라이언트로 전달할 리스트를
     * Dto로 변환하는 메서드
     */
    @Override
    public List<FestivalProgramResponseDto> convertToDto(List<FestivalProgram> festivalProgramList) {
        List<FestivalProgramResponseDto> festivalProgramResponseDtoList = new ArrayList<>();
        for(FestivalProgram festivalProgram : festivalProgramList){
            FestivalProgramResponseDto festivalProgramResponseDto = FestivalProgramResponseDto.builder()
                    .festivalProgramId(festivalProgram.getFestivalProgramId())
                    .title(festivalProgram.getTitle())
                    .countLike(festivalProgram.getCountLike())
                    .startTime(festivalProgram.getStartTime())
                    .endTime(festivalProgram.getEndTime())
                    .build();

            festivalProgramResponseDtoList.add(festivalProgramResponseDto);
        }
        return festivalProgramResponseDtoList;
    }

    /**
     *  클라이언트에서 요청할 날짜가
     *  올바른지 판별하는 메서드
     */
    @Override
    public boolean isCorrectDate(LocalDate dDay){
        if(DDay.FIRST_DATE.getDate().equals(String.valueOf(dDay)) || DDay.SECOND_DATE.getDate().equals(String.valueOf(dDay)) || DDay.LAST_DATE.getDate().equals(String.valueOf(dDay))){
            return true;
        } else{
            return false;
        }
    }
}
