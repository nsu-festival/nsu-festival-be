package com.example.nsu_festival.domain.visitor.controller;

import com.example.nsu_festival.domain.visitor.dto.VisitorRequestDto;
import com.example.nsu_festival.domain.visitor.service.VisitorService;
import com.example.nsu_festival.domain.visitor.service.VisitorServiceImpl;
import com.example.nsu_festival.global.etc.StatusResponseDto;
import com.example.nsu_festival.domain.visitor.dto.VisitorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/visitors")
public class VisitorController {

    private final VisitorService visitorService;

    @GetMapping("/address")
    public ResponseEntity<StatusResponseDto> getUUID(@RequestHeader(value = "visit") String visit) {
        try {
            VisitorRequestDto visitorRequestDto = new VisitorRequestDto(visit);
            String UUID = visitorService.validator(visitorRequestDto);
            return ResponseEntity.ok(StatusResponseDto.success(UUID));

        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StatusResponseDto.addStatus(400));
        }
    }

    @GetMapping()
    public ResponseEntity<StatusResponseDto> getCount() {
        VisitorResponseDto countResponse = visitorService.findVisitor();
        return ResponseEntity.ok(StatusResponseDto.success(countResponse));
    }
}
