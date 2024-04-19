package com.example.nsu_festival.domain.festival.service;

import com.example.nsu_festival.domain.festival.dto.FestivalProgramResponseDto;
import com.example.nsu_festival.domain.festival.entity.DDay;
import com.example.nsu_festival.domain.festival.entity.FestivalDate;
import com.example.nsu_festival.domain.festival.entity.FestivalProgram;
import com.example.nsu_festival.domain.festival.repository.FestivalDateRepository;
import com.example.nsu_festival.domain.festival.repository.FestivalProgramRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
@AllArgsConstructor
public class FestivalProgramServiceImpl implements FestivalProgramService, InitializeDataService{
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

        FestivalProgram festivalProgram1 = new FestivalProgram(1L, "무대준비", "13:00", "15:30", 0, festivalDate1);
        festivalProgramRepository.save(festivalProgram1);

        FestivalProgram festivalProgram2 = new FestivalProgram(2L, "LIGHT NOW!", "15:30", "16:30", 0, festivalDate1);
        festivalProgramRepository.save(festivalProgram2);

        FestivalProgram festivalProgram3 = new FestivalProgram(3L, "울려라! 골든벨", "16:30", "17:00", 0, festivalDate1);
        festivalProgramRepository.save(festivalProgram3);

        FestivalProgram festivalProgram4 = new FestivalProgram(4L, "소리터", "17:00", "17:30", 0, festivalDate1);
        festivalProgramRepository.save(festivalProgram4);

        FestivalProgram festivalProgram5 = new FestivalProgram(5L, "C.O.M.E", "17:30", "18:00", 0, festivalDate1);
        festivalProgramRepository.save(festivalProgram5);

        FestivalProgram festivalProgram6 = new FestivalProgram(6L, "제너시스", "18:00", "18:30", 0, festivalDate1);
        festivalProgramRepository.save(festivalProgram6);

        FestivalProgram festivalProgram7 = new FestivalProgram(7L, "아메리타트", "18:30", "19:00", 0, festivalDate1);
        festivalProgramRepository.save(festivalProgram7);

        FestivalProgram festivalProgram8 = new FestivalProgram(8L, "실용음악학과 공연", "19:00", "19:30", 0, festivalDate1);
        festivalProgramRepository.save(festivalProgram8);

        FestivalProgram festivalProgram9 = new FestivalProgram(9L, "30주년 LED 점등식", "19:30", "20:00", 0, festivalDate1);
        festivalProgramRepository.save(festivalProgram9);

        FestivalProgram festivalProgram10 = new FestivalProgram(10L, "장내 정리", "21:30", "22:00", 0, festivalDate1);
        festivalProgramRepository.save(festivalProgram10);

        FestivalProgram festivalProgram11 = new FestivalProgram(11L, "리허설", "13:00", "15:30", 0, festivalDate2);
        festivalProgramRepository.save(festivalProgram11);

        FestivalProgram festivalProgram12 = new FestivalProgram(12L, "맞히면 뭐하니? 상품주지!", "15:30", "16:30", 0, festivalDate2);
        festivalProgramRepository.save(festivalProgram12);

        FestivalProgram festivalProgram13 = new FestivalProgram(13L, "눈싸람", "16:30", "17:00", 0, festivalDate2);
        festivalProgramRepository.save(festivalProgram13);

        FestivalProgram festivalProgram14 = new FestivalProgram(14L, "외국인 유학생 공연", "17:00", "17:15", 0, festivalDate2);
        festivalProgramRepository.save(festivalProgram14);

        FestivalProgram festivalProgram15 = new FestivalProgram(15L, "나락퀴즈쇼 상영", "17:15", "17:30", 0, festivalDate2);
        festivalProgramRepository.save(festivalProgram15);

        FestivalProgram festivalProgram16 = new FestivalProgram(16L, "노스텔지어", "17:30", "18:00", 0, festivalDate2);
        festivalProgramRepository.save(festivalProgram16);

        FestivalProgram festivalProgram17 = new FestivalProgram(17L, "카르페디엠", "18:00", "18:30", 0, festivalDate2);
        festivalProgramRepository.save(festivalProgram17);

        FestivalProgram festivalProgram18 = new FestivalProgram(18L, "그루브", "18:30", "19:00", 0, festivalDate2);
        festivalProgramRepository.save(festivalProgram18);

        FestivalProgram festivalProgram19 = new FestivalProgram(19L, "저스트댄스", "19:00", "19:30", 0, festivalDate2);
        festivalProgramRepository.save(festivalProgram19);

        FestivalProgram festivalProgram20 = new FestivalProgram(20L, "스콘", "19:30", "20:00", 0, festivalDate2);
        festivalProgramRepository.save(festivalProgram20);

        FestivalProgram festivalProgram21 = new FestivalProgram(21L, "장내 정리", "21:30", "22:00", 0, festivalDate2);
        festivalProgramRepository.save(festivalProgram21);

        FestivalProgram festivalProgram22 = new FestivalProgram(22L, "Light Now!", "14:00", "15:00", 0, festivalDate3);
        festivalProgramRepository.save(festivalProgram22);

        FestivalProgram festivalProgram23 = new FestivalProgram(23L, "졸업생 공연 리허설", "15:00", "15:30", 0, festivalDate3);
        festivalProgramRepository.save(festivalProgram23);

        FestivalProgram festivalProgram24 = new FestivalProgram(24L, "남서울 가요제 리허설", "15:30", "17:00", 0, festivalDate3);
        festivalProgramRepository.save(festivalProgram24);

        FestivalProgram festivalProgram25 = new FestivalProgram(25L, "남서울 가요제 2차 예선", "17:00", "19:00", 0, festivalDate3);
        festivalProgramRepository.save(festivalProgram25);

        FestivalProgram festivalProgram26 = new FestivalProgram(26L, "졸업생 공연", "19:00", "19:40", 0, festivalDate3);
        festivalProgramRepository.save(festivalProgram26);

        FestivalProgram festivalProgram27 = new FestivalProgram(27L, "총학생회 소개", "19:40", "20:00", 0, festivalDate3);
        festivalProgramRepository.save(festivalProgram27);

        FestivalProgram festivalProgram28 = new FestivalProgram(28L, "불꽃놀이", "21:30", "21:35", 0, festivalDate3);
        festivalProgramRepository.save(festivalProgram28);

        FestivalProgram festivalProgram29 = new FestivalProgram(29L, "장내 정리", "21:35", "22:00", 0, festivalDate3);
        festivalProgramRepository.save(festivalProgram29);
    }
}
