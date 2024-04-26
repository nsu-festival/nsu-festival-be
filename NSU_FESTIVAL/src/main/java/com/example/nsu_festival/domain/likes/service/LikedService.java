package com.example.nsu_festival.domain.likes.service;


public interface LikedService {
    boolean toggleLikeContents(Object likeContents);                                         // 좋아요를 누른 유저의 좋아요 여부 저장

    void updateLikeCount(Object likeContents);                                              // 각 테이블 좋아요 개수 업데이트

}
