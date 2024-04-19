package com.example.nsu_festival.domain.likes.service;

import com.example.nsu_festival.domain.festival.entity.FestivalProgram;
import com.example.nsu_festival.domain.festival.repository.FestivalProgramRepository;
import com.example.nsu_festival.domain.likes.entity.FestivalProgramLiked;
import com.example.nsu_festival.domain.likes.repository.FestivalProgramLikedRepository;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FestivalProgramLikedServiceImpl implements LikedService{
    private final FestivalProgramLikedRepository festivalProgramLikedRepository;
    private final FestivalProgramRepository festivalProgramRepository;
    private final UserRepository userRepository;

    /**
     *  좋아요를 누른 사용자의 좋아요 여부 업데이트
     */
    @Override
    @Transactional
    public boolean toggleLikeContents(Object likeContents) {
        try {
            log.info("=== 축제좋아요 여부 업데이트시작 ===");
            FestivalProgramLiked festivalProgramLiked = (FestivalProgramLiked) likeContents;
            festivalProgramLiked.updateFestivalProgramLiked(!festivalProgramLiked.isFestivalProgramLike());
            festivalProgramLikedRepository.save(festivalProgramLiked);
            log.info("=== 축제좋아요 여부 업데이트완료 ===");

            // 좋아요 개수 업데이트
            updateLikeCount(festivalProgramLiked);

            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * 좋아요 테이블의 사용자 기본 레코드 생성
     */
    public void createUserLike(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with userId: " + userId));

        List<FestivalProgram> festivalProgramList = festivalProgramRepository.findAll();
        for(FestivalProgram festivalProgram : festivalProgramList){
            FestivalProgramLiked festivalProgramLiked = FestivalProgramLiked.builder()
                    .isFestivalProgramLike(false)
                    .festivalProgram(festivalProgram)
                    .user(user)
                    .build();

            festivalProgramLikedRepository.save(festivalProgramLiked);
        }

    }

    /**
     * FestivalProgram 테이블의 총 좋아요 개수 업데이트
     */
    @Override
    @Transactional
    public void updateLikeCount(Object likeContents) {
        FestivalProgramLiked festivalProgramLiked = (FestivalProgramLiked) likeContents;
        Long festivalProgramId = festivalProgramLiked.getFestivalProgram().getFestivalProgramId();
        FestivalProgram festivalProgram = festivalProgramRepository.findById(festivalProgramId)
                .orElseThrow(() -> new RuntimeException("없는 축제 프로그램"));

        int count = festivalProgramLikedRepository.countFestivalProgramLike(festivalProgramId);
        log.info("좋아요 개수:{}", count);

        log.info("=== 좋아요 개수 업데이트 ===");
        festivalProgram.updateCountLike(count);

        // 좋아요 개수 업데이트 후 저장
        festivalProgramRepository.save(festivalProgram);
    }
}
