package com.example.nsu_festival.domain.likes.service;

import com.example.nsu_festival.domain.festival.entity.SingerLineup;
import com.example.nsu_festival.domain.festival.repository.SingerLineupRepository;
import com.example.nsu_festival.domain.likes.entity.SingerLineupLiked;
import com.example.nsu_festival.domain.likes.repository.SingerLineupLikedRepository;
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
public class SingerLineupLikedServiceImpl implements LikedService{
    private final SingerLineupLikedRepository singerLineupLikedRepository;
    private final UserRepository userRepository;
    private final SingerLineupRepository singerLineupRepository;
    /**
     *  좋아요를 누른 사용자의 좋아요 여부 업데이트
     */
    @Override
    @Transactional
    public boolean toggleLikeContents(Object likeContents){
        try{
            log.info("=== 가수 라인업 좋아요 여부 업데이트시작 ===");
            SingerLineupLiked singerLineupLiked = (SingerLineupLiked) likeContents;
            singerLineupLiked.updateSingerLineupLiked(!singerLineupLiked.isSingerLineupLike());
            singerLineupLikedRepository.save(singerLineupLiked);
            log.info("=== 가수 라인업 좋아요 여부 업데이트완료 ===");

            // 좋아요 개수 업데이트
            updateLikeCount(singerLineupLiked);
            return true;
        }catch (RuntimeException e){
            return false;
        }
    }

    /**
     * 좋아요 테이블의 사용자 기본 레코드 생성
     */
    public void createUserLike(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with userId: " + userId));

        List<SingerLineup> singerLineupList = singerLineupRepository.findAll();

        for(SingerLineup singerLineup : singerLineupList){
            SingerLineupLiked singerLineupLiked = SingerLineupLiked.builder()
                    .isSingerLineupLike(false)
                    .singerLineup(singerLineup)
                    .user(user)
                    .build();

            singerLineupLikedRepository.save(singerLineupLiked);
        }
    }
    /**
     * SingerLineup 테이블의 총 좋아요 개수 업데이트
     */
    @Override
    public void updateLikeCount(Object likeContents) {
        SingerLineupLiked singerLineupLiked = (SingerLineupLiked) likeContents;
        Long singerLineupId = singerLineupLiked.getSingerLineup().getSingerLineupId();
        SingerLineup singerLineup = singerLineupRepository.findById(singerLineupId)
                .orElseThrow(() -> new RuntimeException("없는 축제 프로그램"));

        int count = singerLineupLikedRepository.countSingerLineupLike(singerLineupId);
        log.info("좋아요 개수:{}", count);

        log.info("=== 좋아요 개수 업데이트 ===");
        singerLineup.updateCountLike(count);

        // 좋아요 개수 업데이트 후 저장
        singerLineupRepository.save(singerLineup);
    }
}
