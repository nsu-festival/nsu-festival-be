package com.example.nsu_festival.domain.visitor.controller;

import com.example.nsu_festival.domain.visitor.service.VisitorService;
import com.example.nsu_festival.domain.visitor.service.VisitorServiceImpl;
import com.example.nsu_festival.global.etc.StatusResponseDto;
import com.example.nsu_festival.domain.visitor.dto.VisitorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/visitors")
public class VisitorController {

    private final VisitorService visitorService;

    @GetMapping("/address")
    public ResponseEntity<StatusResponseDto> getIp(HttpServletRequest request) {
        visitorService.savedVisitor(request);
        return ResponseEntity.ok(StatusResponseDto.success());
    }

    @GetMapping()
    public ResponseEntity<StatusResponseDto> getCount() {
        VisitorResponseDto countResponse = visitorService.findVisitor();
        return ResponseEntity.ok(StatusResponseDto.success(countResponse));
    }
}
