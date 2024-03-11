package com.example.nsu_festival.domain.likes.service;

import com.example.nsu_festival.domain.likes.dto.UserLikeDto;
import com.example.nsu_festival.domain.likes.entity.BoothLiked;
import com.example.nsu_festival.domain.likes.entity.FestivalProgramLiked;
import com.example.nsu_festival.domain.likes.entity.SingerLineupLiked;
import com.example.nsu_festival.domain.likes.repository.BoothLikedRepository;
import com.example.nsu_festival.domain.likes.repository.FestivalProgramLikedRepository;
import com.example.nsu_festival.domain.likes.repository.SingerLineupLikedRepository;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    /**
     *  좋아요 테이블에 해당 유저에 관한
     *  레코드가 생성되어 있는지 판별하는 메서드
     */
    @Override
    public boolean determineUser(String contentType, CustomOAuth2User customOAuth2User) {
        //유저정보 추출
        String userEmail = customOAuth2User.getEmail();
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail))
                .getId();

        // contentType에 해당하는 좋아요테이블에서 유저 검색 후 true, false반환
        if("booth".equals(contentType)){
            return boothLikedRepository.findUserByUserId(userId);
        }else if("festivalProgram".equals(contentType)){
            return festivalProgramLikedRepository.findUserByUserId(userId);
        }else {
            return singerLineupLikedRepository.findUserByUserId(userId);
        }
    }

    /**
     *  사용자가 좋아요를 누른 컨텐츠가 무엇인지 판별 후
     *  해당하는 컨텐츠의 좋아요 업데이트
     */
    @Override
    public boolean determineContents(String contentType, Long contentId){
        log.info("determinecontents -> contentType :{}", contentType);
        log.info("determinecontents -> contentId :{}", contentId);

        log.info("해당하는 좋아요 메서드 호출");
        if("booth".equals(contentType)){
            BoothLiked boothLiked = boothLikedRepository.findBoothLikedByContentId(contentId);
            if(boothLikedService.toggleLikeContents(boothLiked)){
                log.info("호출된 좋아요 메서드 실행 완료");
                return true;
            }
            log.info("호출된 좋아요 메서드 실행 실패");
            return false;
        } else if ("festivalProgram".equals(contentType)) {
            FestivalProgramLiked festivalProgramLiked = festivalProgramLikedRepository.findFestivalProgramLikedByContentId(contentId);
            if(festivalProgramLikedService.toggleLikeContents(festivalProgramLiked)){
                log.info("호출된 좋아요 메서드 실행 완료");
                return true;
            }
            log.info("호출된 좋아요 메서드 실행 실패");
            return false;
        } else {
            SingerLineupLiked singerLineupLiked = singerLineupLikedRepository.findSingerLineupLikedByContentId(contentId);
            if(singerLineupLikedService.toggleLikeContents(singerLineupLiked)){
                log.info("호출된 좋아요 메서드 실행 완료");
                return true;
            }
            log.info("호출된 좋아요 메서드 실행 실패");
            return false;
        }
    }

    /**
     *  현재 사용자의 각 컨텐츠의 좋아요 여부 반환 메서드
     */
    @Override
    public List<UserLikeDto> findUserLike (String contentType, CustomOAuth2User customOAuth2User){
        //유저 정보 추출
        String userEmail = customOAuth2User.getEmail();
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail))
                .getId();

        if("booth".equals(contentType)){
            List<BoothLiked> boothLikedList = boothLikedRepository.findBoothLikeListByUserId(userId);
            List<UserLikeDto> userLikeDtoList = convertToDto(contentType, boothLikedList);
            return userLikeDtoList;
        } else if ("festivalProgram".equals(contentType)) {
            List<FestivalProgramLiked> festivalProgramLikedList = festivalProgramLikedRepository.findFestivalProgramLikeListByUserId(userId);
            List<UserLikeDto> userLikeDtoList = convertToDto(contentType, festivalProgramLikedList);
            return userLikeDtoList;
        } else {
            List<SingerLineupLiked> singerLineupLikedList = singerLineupLikedRepository.findSingerLineupLikedListByUserId(userId);
            List<UserLikeDto> userLikeDtoList = convertToDto(contentType, singerLineupLikedList);
            return  userLikeDtoList;
        }
    }

    /**
     *  현재 사용자의 정보가 해당하는
     *  좋아요 테이블에 없다면 기본 레코드 생성 메서드 호출
     */
    @Override
    public void createUserLike(String contentType, CustomOAuth2User customOAuth2User){
        String userEmail = customOAuth2User.getEmail();
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail))
                .getId();

        if("booth".equals(contentType)){
            log.info("booth");
        }else if("festivalProgram".equals(contentType)){
            festivalProgramLikedService.createUserLike(userId);
        }else {
            singerLineupLikedService.createUserLike(userId);
        }
    }

    /**
     *  클라이언트에 전달할 사용자의 좋아요 여부 Dto 변환
     */
    @Override
    private List<UserLikeDto> convertToDto(String contentType, Object likeContentList){
        log.info("userLikeDto 변환");
        if("booth".equals(contentType)){
            List<BoothLiked> boothLikedList = (List<BoothLiked>) likeContentList;
            List<UserLikeDto> userLikeDtoList = new ArrayList<>();
            for(BoothLiked boothLiked : boothLikedList){
                UserLikeDto userLikeDto = UserLikeDto.builder()
                        .contentName(boothLiked.getBooth().getTitle())
                        .isLike(boothLiked.isBoothLike())
                        .build();
                userLikeDtoList.add(userLikeDto);
            }
            return userLikeDtoList;
        }else if("festivalProgram".equals(contentType)){
            List<FestivalProgramLiked> festivalProgramLikedlist = (List<FestivalProgramLiked>) likeContentList;
            List<UserLikeDto> userLikeDtoList = new ArrayList<>();
            for(FestivalProgramLiked festivalProgramLiked : festivalProgramLikedlist){
                UserLikeDto userLikeDto = UserLikeDto.builder()
                        .contentName(festivalProgramLiked.getFestivalProgram().getTitle())
                        .isLike(festivalProgramLiked.isFestivalProgramLike())
                        .build();
                userLikeDtoList.add(userLikeDto);
            }
            return userLikeDtoList;
        }else {
            List<SingerLineupLiked> singerLineupLikedList = (List<SingerLineupLiked>) likeContentList;
            List<UserLikeDto> userLikeDtoList = new ArrayList<>();
            for(SingerLineupLiked singerLineupLiked : singerLineupLikedList){
                UserLikeDto userLikeDto = UserLikeDto.builder()
                        .contentName(singerLineupLiked.getSingerLineup().getSinger())
                        .isLike(singerLineupLiked.isSingerLineupLike())
                        .build();
                userLikeDtoList.add(userLikeDto);
            }
            return userLikeDtoList;
        }
    }
}
