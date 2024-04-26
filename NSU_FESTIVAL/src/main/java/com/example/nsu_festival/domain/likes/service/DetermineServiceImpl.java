package com.example.nsu_festival.domain.likes.service;

import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.booth.repository.BoothRepository;
import com.example.nsu_festival.domain.festival.entity.DDay;
import com.example.nsu_festival.domain.festival.entity.FestivalDate;
import com.example.nsu_festival.domain.festival.entity.FestivalProgram;
import com.example.nsu_festival.domain.festival.entity.SingerLineup;
import com.example.nsu_festival.domain.festival.repository.FestivalDateRepository;
import com.example.nsu_festival.domain.festival.repository.FestivalProgramRepository;
import com.example.nsu_festival.domain.festival.repository.SingerLineupRepository;
import com.example.nsu_festival.domain.likes.dto.UserLikeDto;
import com.example.nsu_festival.domain.likes.entity.BoothLiked;
import com.example.nsu_festival.domain.likes.entity.ContentType;
import com.example.nsu_festival.domain.likes.entity.FestivalProgramLiked;
import com.example.nsu_festival.domain.likes.entity.SingerLineupLiked;
import com.example.nsu_festival.domain.likes.repository.BoothLikedRepository;
import com.example.nsu_festival.domain.likes.repository.FestivalProgramLikedRepository;
import com.example.nsu_festival.domain.likes.repository.SingerLineupLikedRepository;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 좋아요 관련하여 어떤 컨텐츠인지 판별하고 그에 맞는 메서드 호출이나
 * 데이터를 반환하는 메서드들이 모인 클래스
 */
@Service
@Slf4j
@AllArgsConstructor
public class DetermineServiceImpl implements DetermineService{
    private final UserRepository userRepository;
    private final FestivalProgramLikedRepository festivalProgramLikedRepository;
    private final SingerLineupLikedRepository singerLineupLikedRepository;
    private final FestivalProgramLikedServiceImpl festivalProgramLikedService;
    private final BoothLikedServiceImpl boothLikedService;
    private final BoothLikedRepository boothLikedRepository;
    private final SingerLineupLikedServiceImpl singerLineupLikedService;
    private final BoothRepository boothRepository;
    private final FestivalProgramRepository festivalProgramRepository;
    private final SingerLineupRepository singerLineupRepository;
    private final FestivalDateRepository festivalDateRepository;

    /**
     *  좋아요 테이블에 해당 유저에 관한
     *  레코드가 생성되어 있는지 판별하는 메서드
     */
    @Override
    public boolean determineUser(ContentType contentType, CustomOAuth2User customOAuth2User) {
        // 유저정보 추출
        String userEmail = customOAuth2User.getEmail();
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail))
                .getId();

        // contentType에 따른 분기 처리
        switch (contentType) {
            case festivalProgram:
                return festivalProgramLikedRepository.existsByUserId(userId);
            case singerLineup:
                return singerLineupLikedRepository.existsByUserId(userId);
            default:
                throw new RuntimeException("존재하지 않는 컨텐츠");
        }
    }

    public boolean determineBooth(CustomOAuth2User customOAuth2User, Long boothId){
        String userEmail = customOAuth2User.getEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        Booth booth = boothRepository.findById(boothId).orElseThrow(()-> new RuntimeException("없는 부스"));

        return boothLikedRepository.existsByBoothAndUser(booth ,user);
    }


    /**
     *  사용자가 좋아요를 누른 컨텐츠가 무엇인지 판별 후
     *  해당하는 컨텐츠의 좋아요 업데이트
     */
    @Override
    public boolean determineContents(CustomOAuth2User customOAuth2User, ContentType contentType, Long contentId){

        String userEmail = customOAuth2User.getEmail();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        log.info("해당하는 좋아요 메서드 호출");
        switch (contentType) {
            case booth:
                Booth booth = boothRepository.findBoothByBoothId(contentId);
                BoothLiked boothLiked = boothLikedRepository.findByBoothAndUser(booth, user);
                if(boothLiked == null){
                    throw new NoSuchElementException("없는 좋아요 레코드");
                }
                return boothLikedService.toggleLikeContents(boothLiked);
            case festivalProgram:
                FestivalProgram festivalProgram = festivalProgramRepository.findFestivalProgramByFestivalProgramId(contentId);
                FestivalProgramLiked festivalProgramLiked = festivalProgramLikedRepository.findByUserAndFestivalProgram(user, festivalProgram);
                if(festivalProgramLiked == null){
                    throw new NoSuchElementException("없는 좋아요 레코드");
                }
                return festivalProgramLikedService.toggleLikeContents(festivalProgramLiked);
            case singerLineup:
                SingerLineup singerLineup = singerLineupRepository.findSingerLineupBySingerLineupId(contentId);
                SingerLineupLiked singerLineupLiked = singerLineupLikedRepository.findByUserAndSingerLineup(user, singerLineup);
                if(singerLineupLiked == null){
                    throw new NoSuchElementException("없는 좋아요 레코드");
                }
                return singerLineupLikedService.toggleLikeContents(singerLineupLiked);
            default:
                throw new RuntimeException("존재하지 않는 컨텐츠");
        }
    }

    /**
     *  현재 사용자의 각 컨텐츠의 좋아요 여부 반환 메서드
     */
    @Override
    public List<UserLikeDto> findUserLike (ContentType contentType, CustomOAuth2User customOAuth2User, LocalDate day){
        //유저 정보 추출
        String userEmail = customOAuth2User.getEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        FestivalDate festivalDate = festivalDateRepository.findByDDay(day);
        switch (contentType) {
            case festivalProgram:
                List<FestivalProgram> festivalProgramList = festivalProgramRepository.findAllByFestivalDate(festivalDate);
                List<FestivalProgramLiked> festivalProgramLikedList = new ArrayList<>();
                for (FestivalProgram festivalProgram : festivalProgramList) {
                    FestivalProgramLiked festivalProgramLiked = festivalProgramLikedRepository.findByUserAndFestivalProgram(user, festivalProgram);
                    festivalProgramLikedList.add(festivalProgramLiked);
                }
                return convertToDto(contentType, festivalProgramLikedList);
            case singerLineup:
                List<SingerLineup> singerLineupList = singerLineupRepository.findAllByFestivalDate(festivalDate);
                List<SingerLineupLiked> singerLineupLikedList = new ArrayList<>();
                for (SingerLineup singerLineup : singerLineupList) {
                    SingerLineupLiked singerLineupLiked = singerLineupLikedRepository.findByUserAndSingerLineup(user, singerLineup);
                    singerLineupLikedList.add(singerLineupLiked);
                }
                return convertToDto(contentType, singerLineupLikedList);
            default:
                throw new RuntimeException("존재하지 않는 컨텐츠");
        }
    }


    /**
     * 인가되지 않은 사용자의
     * 좋아요 여부
     * 모든 좋아요 여부는 false
     */
    @Override
    public List<UserLikeDto> findNoUserLike(ContentType contentType, LocalDate dDay) {
        FestivalDate festivalDate = festivalDateRepository.findByDDay(dDay);
        switch (contentType) {
            case festivalProgram:
                List<FestivalProgram> festivalProgramList = festivalProgramRepository.findAllByFestivalDate(festivalDate);
                return convertToDtoNoUser(contentType, festivalProgramList);
            case singerLineup:
                List<SingerLineup> singerLineupList = singerLineupRepository.findAllByFestivalDate(festivalDate);
                return convertToDtoNoUser(contentType, singerLineupList);
            default:
                throw new RuntimeException("존재하지 않는 컨텐츠");
        }
    }

    /**
     *  현재 사용자의 정보가 해당하는
     *  좋아요 테이블에 없다면 기본 레코드 생성 메서드 호출
     */
    @Override
    public void createUserLike(ContentType contentType, CustomOAuth2User customOAuth2User) {
        String userEmail = customOAuth2User.getEmail();
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail))
                .getId();

        switch (contentType) {
            case festivalProgram:
                festivalProgramLikedService.createUserLike(userId);
                break;
            case singerLineup:
                singerLineupLikedService.createUserLike(userId);
                break;
            default:
                throw new RuntimeException("존재하지 않는 컨텐츠");
        }
    }

    /**
     *  클라이언트에 전달할 사용자의 좋아요 여부 Dto 변환
     */
    @Override
    public List<UserLikeDto> convertToDto(ContentType contentType, Object likeContentList) {
        log.info("userLikeDto 변환");
        List<UserLikeDto> userLikeDtoList = new ArrayList<>();
        switch (contentType) {
            case festivalProgram:
                List<FestivalProgramLiked> festivalProgramLikedlist = (List<FestivalProgramLiked>) likeContentList;
                for (FestivalProgramLiked festivalProgramLiked : festivalProgramLikedlist) {
                    UserLikeDto userLikeDto = UserLikeDto.builder()
                            .contentName(festivalProgramLiked.getFestivalProgram().getTitle())
                            .isLike(festivalProgramLiked.isFestivalProgramLike())
                            .build();
                    userLikeDtoList.add(userLikeDto);
                }
                return userLikeDtoList;
            case singerLineup:
                List<SingerLineupLiked> singerLineupLikedList = (List<SingerLineupLiked>) likeContentList;
                for (SingerLineupLiked singerLineupLiked : singerLineupLikedList) {
                    UserLikeDto userLikeDto = UserLikeDto.builder()
                            .contentName(singerLineupLiked.getSingerLineup().getSinger())
                            .isLike(singerLineupLiked.isSingerLineupLike())
                            .build();
                    userLikeDtoList.add(userLikeDto);
                }
                return userLikeDtoList;
            default:
                throw new RuntimeException("존재하지 않는 컨텐츠");
        }
    }

    /**
     *  인가되지 않은 사용자의
     *  클라이언트로 전달할 dto 변환
     *  모든 isLike false
     */
    public List<UserLikeDto> convertToDtoNoUser(ContentType contentType, Object likeContentList) {
        log.info("userLikeDto 변환");
        List<UserLikeDto> userLikeDtoList = new ArrayList<>();
        switch (contentType) {
            case festivalProgram:
                List<FestivalProgram> festivalProgramList = (List<FestivalProgram>) likeContentList;
                for (FestivalProgram festivalProgram : festivalProgramList) {
                    UserLikeDto userLikeDto = UserLikeDto.builder()
                            .contentName(festivalProgram.getTitle())
                            .isLike(false)
                            .build();
                    userLikeDtoList.add(userLikeDto);
                }
                return userLikeDtoList;
            case singerLineup:
                List<SingerLineup> singerLineupList = (List<SingerLineup>) likeContentList;
                for (SingerLineup singerLineup : singerLineupList) {
                    UserLikeDto userLikeDto = UserLikeDto.builder()
                            .contentName(singerLineup.getSinger())
                            .isLike(false)
                            .build();
                    userLikeDtoList.add(userLikeDto);
                }
                return userLikeDtoList;
            default:
                throw new RuntimeException("존재하지 않는 컨텐츠");
        }
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
}
