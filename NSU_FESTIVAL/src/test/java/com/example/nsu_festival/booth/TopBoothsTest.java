//package com.example.nsu_festival.booth;
//
//import com.example.nsu_festival.domain.booth.dto.TopBoothResponseDto;
//import com.example.nsu_festival.domain.booth.entity.Booth;
//import com.example.nsu_festival.domain.booth.repository.BoothRepository;
//import com.example.nsu_festival.domain.booth.service.BoothServiceImpl;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import static org.assertj.core.api.Assertions.*;
//
//@SpringBootTest
//public class TopBoothsTest {
//
//    @Autowired
//    BoothRepository boothRepository;
//    @Autowired
//    BoothServiceImpl boothService;
//
//
//    @Test
//    @DisplayName("탑부스")
//    @Transactional
//    void topBooths(){
//        //given
//        Booth computer = boothRepository.findBoothByBoothId(7L);
//        computer.updateCountLike(5);
//        boothRepository.save(computer);
//
//        Booth human = boothRepository.findBoothByBoothId(53L);
//        human.updateCountLike(4);
//        boothRepository.save(human);
//
//        Booth SHOUT = boothRepository.findBoothByBoothId(55L);
//        SHOUT.updateCountLike(3);
//        boothRepository.save(SHOUT);
//
//        Booth healthAdministration = boothRepository.findBoothByBoothId(17L);
//        healthAdministration.updateCountLike(100);
//        boothRepository.save(healthAdministration);
//
//        Booth medical = boothRepository.findBoothByBoothId(32L);
//        medical.updateCountLike(50);
//        boothRepository.save(medical);
//
//
//        //when
//        List<TopBoothResponseDto> topBooths = boothService.findTopBooths();
//
//        //then
//        assertThat(topBooths.get(0).getTitle()).isEqualTo("보건행정");
//        assertThat(topBooths.get(1).getTitle()).isEqualTo("간호");
//        assertThat(topBooths.get(2).getTitle()).isEqualTo("컴퓨터 소프트 웨어");
//        assertThat(topBooths.get(3).getTitle()).isEqualTo("아름다운 사람들");
//        assertThat(topBooths.get(4).getTitle()).isEqualTo("SHOUT");
//    }
//
//}
