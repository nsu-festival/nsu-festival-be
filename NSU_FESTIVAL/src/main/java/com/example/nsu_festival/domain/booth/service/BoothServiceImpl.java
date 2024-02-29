package com.example.nsu_festival.domain.booth.service;


import com.example.nsu_festival.domain.booth.dto.BoothDto;
import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.booth.repository.BoothRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoothServiceImpl implements BoothService{


    private final BoothRepository boothRepository;
   private final ModelMapper modelMapper;
    public List<BoothDto> getAllBooths(){
        List<BoothDto> boothDtoLists = boothRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return boothDtoLists;
    }

    private BoothDto convertToDto(Booth booth){
        return modelMapper.map(booth,BoothDto.class);
    }
}
