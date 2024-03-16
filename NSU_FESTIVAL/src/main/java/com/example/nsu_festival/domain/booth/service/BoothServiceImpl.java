package com.example.nsu_festival.domain.booth.service;

import com.example.nsu_festival.domain.booth.dto.BoothCommentDto;
import com.example.nsu_festival.domain.booth.dto.BoothDetailDto;
import com.example.nsu_festival.domain.booth.dto.BoothDto;
import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.booth.entity.Menu;
import com.example.nsu_festival.domain.booth.repository.BoothRepository;
import com.example.nsu_festival.domain.booth.repository.MenuRepository;
import com.example.nsu_festival.domain.comment.entity.Comment;
import com.example.nsu_festival.domain.comment.repository.CommentRepository;
import com.example.nsu_festival.domain.likes.entity.BoothLiked;
import com.example.nsu_festival.domain.likes.repository.BoothLikedRepository;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.exception.CustomException;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.example.nsu_festival.global.exception.ExceptionCode.SERVER_ERROR;

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
    private final MenuRepository menuRepository;
    /**
     *
     * 부스리스트 조회
     */

    public List<BoothDto> getAllBooths(){
        try{
            List<BoothDto> boothDtoLists = boothRepository.findAll().stream()
                    .filter(booth -> booth.getBoothCategories().stream().noneMatch(category->category.getCategory().equals("푸드트럭")))
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return boothDtoLists;
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new CustomException(SERVER_ERROR);
        }

    }

    /**
     *
     * 푸드트럭 리스트 조회
     */

    public List<BoothDto> getAllFoodTrucks() {
        try {
            List<BoothDto> allBooths = boothRepository.findAll().stream().
                    filter(boothDto -> boothDto.getBoothCategories().stream()
                            .anyMatch(category -> category.getCategory().equals("푸드트럭")))
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return allBooths;
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new CustomException(SERVER_ERROR);
        }

    }

    private BoothDto convertToDto(Booth booth){
        return modelMapper.map(booth,BoothDto.class);
    }

    /**
     *
     * 부스 상세페이지 조회
     */
    public BoothDetailDto getDetailBooth(Long boothId, CustomOAuth2User customOAuth2User) {
        try{
            Booth booth = boothRepository.findById(boothId).get();
            User user = userRepository.findByEmail(customOAuth2User.getEmail()).get();
            List<Menu> menu = menuRepository.findMenusByBooth(booth);
            BoothLiked boothLiked = boothLikedRepository.findBoothLikedByUser(user);

            List<Comment> comments = commentRepository.findAllCommentByBooth(booth);
            Long boothCommentCounts = commentRepository.countCommentByBooth(booth);


            List<BoothCommentDto> commentDtos = new ArrayList<>();

            for (Comment boothComment : comments) {
                User commentUser = boothComment.getUser();
                if (commentUser != null) {
                    String userName = commentUser.getNickName();
                    if (userName.length() >= 2) {
                        String maskedName = userName.substring(0, 1) + "*" + userName.substring(2);
                        BoothCommentDto commentDto = new BoothCommentDto();
                        commentDto.setCommentId(boothComment.getCommentId());
                        commentDto.setContent(boothComment.getContent());
                        commentDto.setUserName(maskedName);
                        commentDtos.add(commentDto);
                    } else {
                        BoothCommentDto commentDto = new BoothCommentDto();
                        commentDto.setCommentId(boothComment.getCommentId());
                        commentDto.setContent(boothComment.getContent());
                        commentDto.setUserName(userName);
                        commentDtos.add(commentDto);
                    }
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
                    .menus(menu)
                    .build();
            return boothDetailDto;

        }
        catch (RuntimeException e){
            e.printStackTrace();
            throw new CustomException(SERVER_ERROR);
        }

    }

}
