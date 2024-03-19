package com.example.nsu_festival.domain.notice.service;

import com.example.nsu_festival.domain.notice.dto.NoticeRequestDto;
import com.example.nsu_festival.domain.notice.dto.NoticeResponseDto;
import com.example.nsu_festival.domain.notice.entity.Notice;
import com.example.nsu_festival.domain.notice.repository.NoticeRepository;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class NoticeServiceImpl implements NoticeService{
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final ModelMapper modelMapper;


    /**
     * 공지사항 목록 반환 메서드
     */
    @Override
    public List<NoticeResponseDto> findAllNoticeList(){
        try{
            List<NoticeResponseDto> noticeResponseDtoList = noticeRepository.findAll().stream()
                    .map(notice -> {
                        NoticeResponseDto dto = convertToDto(notice);
                        dto.setContent(null);
                        return dto;
                    })
                    .collect(Collectors.toList());
            return noticeResponseDtoList;
        } catch (RuntimeException e){
            throw new RuntimeException();
        }
    }

    /**
     *  공지사항 단일조회 메서드
     */
    @Override
    public NoticeResponseDto findNoticeDetail(Long noticeId){
        try{
            Notice notice = noticeRepository.findById(noticeId).orElseThrow(()->new RuntimeException());
            NoticeResponseDto noticeResponseDto = modelMapper.map(notice, NoticeResponseDto.class);
            return noticeResponseDto;
        } catch (RuntimeException e){
            throw new RuntimeException();
        }
    }

    /**
     * 공지사항 작성 메서드
     */
    @Override
    public void writeNotice(NoticeRequestDto noticeRequestDto, CustomOAuth2User customOAuth2User) {
        if (customOAuth2User != null && isAdmin(customOAuth2User)) {
            String email = customOAuth2User.getEmail();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            Notice notice = Notice.builder()
                    .creatAt(LocalDateTime.now())
                    .title(noticeRequestDto.getTitle())
                    .content(noticeRequestDto.getContent())
                    .user(user)
                    .build();
            noticeRepository.save(notice);
        } else {
            throw new HttpClientErrorException(HttpStatusCode.valueOf(401));
        }
    }

    /**
     *  공지사항
     */
    @Override
    public void updateNotice(Long noticeId, NoticeRequestDto noticeRequestDto, CustomOAuth2User customOAuth2User) {
        if (customOAuth2User != null && isAdmin(customOAuth2User)) {
            String email = customOAuth2User.getEmail();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new RuntimeException("Notice not found with noticeId: " + noticeId));;
            notice.updateNotice(noticeRequestDto, LocalDateTime.now(), user);
            noticeRepository.save(notice);
        } else {
            throw new HttpClientErrorException(HttpStatusCode.valueOf(401));
        }
    }

    @Override
    public void deleteNotice(Long noticeId, CustomOAuth2User customOAuth2User){
        try{
            if (customOAuth2User != null && isAdmin(customOAuth2User)) {
                noticeRepository.deleteById(noticeId);
            }else{
                throw new HttpClientErrorException(HttpStatusCode.valueOf(401));
            }
        }catch (HttpClientErrorException e){
            throw new HttpClientErrorException(HttpStatusCode.valueOf(401));
        }catch (RuntimeException e){
            throw new RuntimeException();
        }

    }

    /**
     * 관리자인지 판별하는 메서드
     */
    private boolean isAdmin(CustomOAuth2User customOAuth2User){
        String email = customOAuth2User.getEmail();
        if("admin".equals(userRepository.findRoleByEmail(email))){
            return true;
        }
        return false;
    }

    /**
     * 공지사항 목록 반환할 dto로 변환시켜주는 메서드
     */
    private NoticeResponseDto convertToDto(Notice notice){
        return modelMapper.map(notice, NoticeResponseDto.class);
    }
}
