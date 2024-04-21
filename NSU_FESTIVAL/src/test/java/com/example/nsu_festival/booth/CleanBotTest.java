package com.example.nsu_festival.booth;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class CleanBotTest {


    @Test
    @DisplayName("클린봇_이름_테스트")
    void cleanBot(){
        //given
        String nickName1 = "클린봇";
        String nickName2 = "권영태";

        //when
        nickName1 = filtering(nickName1);
        nickName2 = filtering(nickName2);

        //then
        assertThat(nickName1).isEqualTo("클린봇");
        assertThat(nickName2).isEqualTo("권*태");
    }

    static String filtering(String nickName){
        if(nickName.length() >= 2 && !"클린봇".equals(nickName)){
            nickName = nickName.substring(0, 1) + "*" + nickName.substring(2);
        }
        return nickName;
    }
}
