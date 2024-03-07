package com.example.nsu_festival.domain.booth.service;

import com.example.nsu_festival.domain.booth.dto.BoothCommentDto;
import com.example.nsu_festival.domain.booth.dto.BoothDetailDto;
import com.example.nsu_festival.domain.booth.dto.BoothDto;
import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.booth.repository.BoothRepository;
import com.example.nsu_festival.domain.comment.entity.Comment;
import com.example.nsu_festival.domain.comment.repository.CommentRepository;
import com.example.nsu_festival.domain.likes.entity.BoothLiked;
import com.example.nsu_festival.domain.likes.repository.BoothLikedRepository;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoothServiceImpl implements BoothService{


    private final BoothRepository boothRepository;
   private final ModelMapper modelMapper;
   private final UserRepository userRepository;
   private final BoothLikedRepository boothLikedRepository;
   private final CommentRepository commentRepository;

    /**
     *
     * 부스리스트 조회
     */

    public List<BoothDto> getAllBooths(){
        List<BoothDto> boothDtoLists = boothRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return boothDtoLists;
    }

    private BoothDto convertToDto(Booth booth){
        return modelMapper.map(booth,BoothDto.class);
    }

    /**
     *
     * 부스 상세페이지 조회
     */
    public BoothDetailDto getDetailBooth(Long boothId, CustomOAuth2User customOAuth2User) {
        Booth booth = boothRepository.findById(boothId).get();
        User user = userRepository.findByEmail(customOAuth2User.getEmail()).get();
        BoothLiked boothLiked = boothLikedRepository.findBoothLikedByUser(user);

        List<Comment> comments = commentRepository.findAllCommentByBooth(booth);
        Long boothCommentCounts = commentRepository.countCommentByBooth(booth);


        List<BoothCommentDto> commentDtos = new ArrayList<>();

        for (Comment boothComment : comments) {
            User commentUser = boothComment.getUser();
            if (commentUser != null) {

                BoothCommentDto commentDto = new BoothCommentDto();
                commentDto.setCommentId(boothComment.getCommentId());
                commentDto.setContent(boothComment.getContent());
                commentDto.setUserName(commentUser.getNickName());


                commentDtos.add(commentDto);
            }
        }

        BoothDetailDto boothDetailDto = BoothDetailDto.builder()
                .boothId(booth.getBoothId())
                .area(booth.getArea())
                .title(booth.getTitle())
                .countLike(booth.getCountLike())
                .content(booth.getContent())
                .boothImage(booth.getBoothImage())
                .boothCategories(booth.getBoothCategories())
                .boothLiked(boothLiked)
                .comments(commentDtos)
                .boothCommentCount(boothCommentCounts)
                .build();
        return boothDetailDto;

    }

}
