package com.example.nsu_festival.global.etc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) //DTO를 JSON으로 변환 시 null값인 field 제외
public class StatusResponseDto {
    private Integer status;
    private Object data;

    public StatusResponseDto(Integer status) {
        this.status = status;
    }

    public StatusResponseDto(Object data) { this.data = data;}

    //이외 상태 코드
    public static StatusResponseDto addStatus(Integer status) {
        return new StatusResponseDto(status);
    }

    //성공 코드(200)
    public static StatusResponseDto success(){
        return new StatusResponseDto(200);
    }

    //성공 코드(200 + data)
    public static StatusResponseDto success(Object data){
        return new StatusResponseDto(200, data);
    }

    //실패 코드(400 + data)
    public static StatusResponseDto fail(Object data){
        return new StatusResponseDto(400, data);
    }

    public static StatusResponseDto error(Object data) {return new StatusResponseDto(data);}
}