package com.example.nsu_festival.like;

import com.example.nsu_festival.domain.festival.entity.SingerLineup;
import com.example.nsu_festival.domain.festival.repository.SingerLineupRepository;
import com.example.nsu_festival.domain.likes.entity.SingerLineupLiked;
import com.example.nsu_festival.domain.likes.repository.SingerLineupLikedRepository;
import com.example.nsu_festival.domain.likes.service.SingerLineupLikedServiceImpl;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource("classpath:application-test.properties") //test용 properties 파일 설정
public class LikeTest {

    @Autowired
    SingerLineupRepository singerLineupRepository;
    @Autowired
    SingerLineupLikedRepository singerLineupLikedRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    SingerLineupLikedServiceImpl singerLineupLikedService;

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

//    @Test
//    @DisplayName("좋아요 토글")
//    @Transactional
//    void toggleLlike(){
//        User user = userRepository.findById(1L).orElseThrow();
//        User user2 = userRepository.findById(2L).orElseThrow();
//        User user3 = userRepository.findById(3L).orElseThrow();
//        User user4 = userRepository.findById(4L).orElseThrow();
//        User user5 = userRepository.findById(5L).orElseThrow();
//        SingerLineup singerLineup = singerLineupRepository.findById(1L).orElseThrow();
//        SingerLineupLiked singerLineupLiked = singerLineupLikedRepository.findByUserAndSingerLineup(user, singerLineup);
//        SingerLineupLiked singerLineupLiked2 = singerLineupLikedRepository.findByUserAndSingerLineup(user2, singerLineup);
//        SingerLineupLiked singerLineupLiked3 = singerLineupLikedRepository.findByUserAndSingerLineup(user3, singerLineup);
//        SingerLineupLiked singerLineupLiked4 = singerLineupLikedRepository.findByUserAndSingerLineup(user4, singerLineup);
//        SingerLineupLiked singerLineupLiked5 = singerLineupLikedRepository.findByUserAndSingerLineup(user5, singerLineup);
//        for(int i = 0; i <51; i++){
//            singerLineupLikedService.toggleLikeContents(singerLineupLiked);
//            singerLineupLikedService.toggleLikeContents(singerLineupLiked2);
//            singerLineupLikedService.toggleLikeContents(singerLineupLiked3);
//            singerLineupLikedService.toggleLikeContents(singerLineupLiked4);
//            singerLineupLikedService.toggleLikeContents(singerLineupLiked5);
//        }
//    }
}

