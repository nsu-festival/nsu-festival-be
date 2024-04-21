package com.example.nsu_festival.domain.notice.exception;

import com.example.nsu_festival.domain.notice.controller.NoticeController;
import com.example.nsu_festival.global.etc.StatusResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * NoticeController의 유효성검사 실패시 발생하는 예외처리를 위한 클래스
 * ControllerAdvice만 사용하면 발생하는 모든 예외를 처리하게 되기 때문에
 * assignalbleTypes로 NoticeController에서 발생하는 에러만처리
 */
@ControllerAdvice(assignableTypes = {NoticeController.class})
@Slf4j
public class ValidExceptionHandler {
    /*@Valid로 인해 발생하는 예외처리응답*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StatusResponseDto> notValidException(MethodArgumentNotValidException e){
        log.error(e.getMessage());
        return ResponseEntity.status(400).body(StatusResponseDto.addStatus(400));
    }
}
