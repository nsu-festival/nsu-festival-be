package com.example.nsu_festival.domain.likes.service;

import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.booth.repository.BoothRepository;
import com.example.nsu_festival.domain.likes.entity.BoothLiked;
import com.example.nsu_festival.domain.likes.repository.BoothLikedRepository;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BoothLikedServiceImpl implements LikedService{
    private final UserRepository userRepository;
    private final BoothRepository boothRepository;
    private final BoothLikedRepository boothLikedRepository;

    /**
     *  좋아요를 누른 사용자의 좋아요 여부 업데이트
     */
    @Override
    @Transactional
    public boolean toggleLikeContents(Object likeContents) {
        try {
            log.info("=== 축제좋아요 여부 업데이트시작 ===");
            BoothLiked boothLiked = (BoothLiked) likeContents;
            boothLiked.updateBoothLiked(!boothLiked.isBoothLike());
            log.info("=== 축제좋아요 여부 업데이트완료 ===");

            // 좋아요 개수 업데이트 메서드 호출
            updateLikeCount(boothLiked);

            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * 좋아요 테이블의 사용자 기본 레코드 생성
     */
    @Override
    public void createUserLike(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with userId: " + userId));

        List<Booth> boothList = boothRepository.findAll();
        for(Booth booth : boothList){
            BoothLiked boothLiked = BoothLiked.builder()
                    .isBoothLike(false)
                    .booth(booth)
                    .user(user)
                    .build();

            boothLikedRepository.save(boothLiked);
        }
    }

    /**
     * Booth테이블의 총 좋아요 개수 업데이트
     */
    @Override
    @Transactional
    public void updateLikeCount(Object likeContents) {
        BoothLiked boothLiked = (BoothLiked) likeContents;
        Long boothId = boothLiked.getBooth().getBoothId();
        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new RuntimeException("없는 축제 프로그램"));

        int count = boothLikedRepository.countBoothLike(boothId);
        log.info("좋아요 개수:{}", count);

        log.info("=== 좋아요 개수 업데이트 ===");
        booth.updateCountLike(count);

        // 좋아요 개수 업데이트 후 저장
        boothRepository.save(booth);
    }
}
