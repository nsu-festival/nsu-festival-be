//package com.example.nsu_festival.like;
//
//import com.example.nsu_festival.domain.festival.entity.SingerLineup;
//import com.example.nsu_festival.domain.festival.repository.SingerLineupRepository;
//import com.example.nsu_festival.domain.likes.entity.SingerLineupLiked;
//import com.example.nsu_festival.domain.likes.repository.SingerLineupLikedRepository;
//import com.example.nsu_festival.domain.user.entity.User;
//import com.example.nsu_festival.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.StopWatch;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//@SpringBootTest
//public class LikeTest {
//
//    @Autowired
//    SingerLineupRepository singerLineupRepository;
//    @Autowired
//    SingerLineupLikedRepository singerLineupLikedRepository;
//    @Autowired
//    UserRepository userRepository;
//
//    @BeforeEach
//    void init(){
//        SingerLineup bibi = SingerLineup.builder()
//                .singerLineupId(1L)
//                .singer("비비")
//                .startTime(LocalTime.now())
//                .endTime(LocalTime.now())
//                .countLike(0)
//                .build();
//        singerLineupRepository.save(bibi);
//        User kim = User.builder()
//                .id(1L)
//                .nickName("kim")
//                .role("ROLE_USER")
//                .email("Kim@naver.com")
//                .build();
//        userRepository.save(kim);
//        User LEE = User.builder()
//                .id(2L)
//                .nickName("LEE")
//                .role("ROLE_USER")
//                .email("Lee@naver.com")
//                .build();
//        userRepository.save(LEE);
//
//        for(int i = 0; i < 100000; i++){
//            SingerLineupLiked singerLineupLiked = SingerLineupLiked.builder()
//                    .userSingerLineupId((long) i)
//                    .singerLineup(bibi)
//                    .user(kim)
//                    .isSingerLineupLike(true)
//                    .build();
//            singerLineupLikedRepository.save(singerLineupLiked);
//        }
//    }
//
//    @Test
//    @DisplayName("좋아요_개수_시간_테스트")
//    @Transactional
//    void likeCount() {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        singerLineupLikedRepository.countSingerLineupLike(1L);
//        stopWatch.stop();
//        System.out.println("stopWatch.getTotalTimeSeconds() = " + stopWatch.getTotalTimeSeconds());
//    }
//}
