package com.example.nsu_festival.domain.likes.service;

import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.booth.repository.BoothRepository;
import com.example.nsu_festival.domain.festival.entity.FestivalProgram;
import com.example.nsu_festival.domain.festival.entity.SingerLineup;
import com.example.nsu_festival.domain.festival.repository.FestivalProgramRepository;
import com.example.nsu_festival.domain.festival.repository.SingerLineupRepository;
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
    private final BoothRepository boothRepository;
    private final FestivalProgramRepository festivalProgramRepository;
    private final SingerLineupRepository singerLineupRepository;
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
//            return boothLikedRepository.existsByUserId(userId);
            return false;
        }else if("festivalProgram".equals(contentType)){
            return festivalProgramLikedRepository.existsByUserId(userId);
        }else if("singerLineup".equals(contentType)){
            return singerLineupLikedRepository.existsByUserId(userId);
        }else{
            throw new RuntimeException("존재하지 않는 컨텐츠");
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
//            BoothLiked boothLiked = boothLikedRepository.findBoothLikedByContentId(contentId);
//            if(boothLikedService.toggleLikeContents(boothLiked)){
//                log.info("호출된 좋아요 메서드 실행 완료");
//                return true;
//            }
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
        } else if("singerLineup".equals(contentType)){
            SingerLineupLiked singerLineupLiked = singerLineupLikedRepository.findSingerLineupLikedByContentId(contentId);
            if(singerLineupLikedService.toggleLikeContents(singerLineupLiked)){
                log.info("호출된 좋아요 메서드 실행 완료");
                return true;
            }
            log.info("호출된 좋아요 메서드 실행 실패");
            return false;
        }
        else{
            throw new RuntimeException("존재하지 않는 컨텐츠");
        }
    }

    /**
     *  현재 사용자의 각 컨텐츠의 좋아요 여부 반환 메서드
     */
    @Override
    public List<UserLikeDto> findUserLike (String contentType, CustomOAuth2User customOAuth2User, int day){
        //유저 정보 추출
        String userEmail = customOAuth2User.getEmail();
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail))
                .getId();

        if("booth".equals(contentType)){
//            List<BoothLiked> boothLikedList = boothLikedRepository.findBoothLikeListByUserId(userId);
//            return convertToDto(contentType, boothLikedList);
            return null;
        } else if ("festivalProgram".equals(contentType)) {
            List<FestivalProgramLiked> festivalProgramLikedList = festivalProgramLikedRepository.findFestivalProgramLikedListByUserId(userId, day);
            return convertToDto(contentType, festivalProgramLikedList);
        } else if("singerLineup".equals(contentType)){
            List<SingerLineupLiked> singerLineupLikedList = singerLineupLikedRepository.findSingerLineupLikedListByUserId(userId, day);
            return convertToDto(contentType, singerLineupLikedList);
        } else{
            throw new RuntimeException("존재하지 않는 컨텐츠");
        }
    }

    /**
     * 인가되지 않은 사용자의
     * 좋아요 여부
     * 모든 좋아요 여부는 false
     */
    public List<UserLikeDto> findNoUserLike(String contentType){
        if("booth".equals(contentType)){
//            List<Booth> boothList = boothRepository.findAll();
//            return convertToDtoNoUser(contentType, boothList);
            return null;
        } else if ("festivalProgram".equals(contentType)) {
            List<FestivalProgram> festivalProgramList = festivalProgramRepository.findAll();
            return convertToDtoNoUser(contentType, festivalProgramList);
        } else if("singerLineup".equals(contentType)){
            List<SingerLineup> singerLineupList = singerLineupRepository.findAll();
            return convertToDtoNoUser(contentType, singerLineupList);
        } else{
            throw new RuntimeException("존재하지 않는 컨텐츠");
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
            boothLikedService.createUserLike(userId);
        }else if("festivalProgram".equals(contentType)){
            festivalProgramLikedService.createUserLike(userId);
        }else if("singerLineup".equals(contentType)){
            singerLineupLikedService.createUserLike(userId);
        } else{
            throw new RuntimeException("존재하지 않는 컨텐츠");
        }
    }

    /**
     *  클라이언트에 전달할 사용자의 좋아요 여부 Dto 변환
     */
    @Override
    public List<UserLikeDto> convertToDto(String contentType, Object likeContentList){
        log.info("userLikeDto 변환");
        if("booth".equals(contentType)){
//            List<BoothLiked> boothLikedList = (List<BoothLiked>) likeContentList;
//            List<UserLikeDto> userLikeDtoList = new ArrayList<>();
//            for(BoothLiked boothLiked : boothLikedList){
//                UserLikeDto userLikeDto = UserLikeDto.builder()
//                        .contentName(boothLiked.getBooth().getTitle())
//                        .isLike(boothLiked.isBoothLike())
//                        .build();
//                userLikeDtoList.add(userLikeDto);
//            }
//            return userLikeDtoList;
            return null;
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

    public List<UserLikeDto> convertToDtoNoUser(String contentType, Object likeContentList) {
        log.info("userLikeDto 변환");
        if ("booth".equals(contentType)) {
//            List<Booth> boothList = (List<Booth>) likeContentList;
//            List<UserLikeDto> userLikeDtoList = new ArrayList<>();
//            for (Booth booth : boothList) {
//                UserLikeDto userLikeDto = UserLikeDto.builder()
//                        .contentName(booth.getTitle())
//                        .isLike(false)
//                        .build();
//                userLikeDtoList.add(userLikeDto);
//            }
//            return userLikeDtoList;
            return null;
        } else if ("festivalProgram".equals(contentType)) {
            List<FestivalProgram> festivalProgramList = (List<FestivalProgram>) likeContentList;
            List<UserLikeDto> userLikeDtoList = new ArrayList<>();
            for (FestivalProgram festivalProgram : festivalProgramList) {
                UserLikeDto userLikeDto = UserLikeDto.builder()
                        .contentName(festivalProgram.getTitle())
                        .isLike(false)
                        .build();
                userLikeDtoList.add(userLikeDto);
            }
            return userLikeDtoList;
        } else {
            List<SingerLineup> singerLineupList = (List<SingerLineup>) likeContentList;
            List<UserLikeDto> userLikeDtoList = new ArrayList<>();
            for (SingerLineup singerLineup : singerLineupList) {
                UserLikeDto userLikeDto = UserLikeDto.builder()
                        .contentName(singerLineup.getSinger())
                        .isLike(false)
                        .build();
                userLikeDtoList.add(userLikeDto);
            }
            return userLikeDtoList;
        }
    }
}
