package com.example.nsu_festival.domain.visitor.service;

import com.example.nsu_festival.domain.visitor.dto.VisitorRequestDto;
import com.example.nsu_festival.domain.visitor.dto.VisitorResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface VisitorService {

    String generateUUID();

    VisitorResponseDto findVisitor();

    String validator(VisitorRequestDto visitorRequestDto);

    String validate(String token);
}
