package com.example.nsu_festival.domain.comment.entity;

import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.comment.dto.CommentUpdateDto;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.global.baseEntity.BaseTimeRegDateEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Comment extends BaseTimeRegDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "content", length = 100)
    private String content;

    @JsonBackReference //순환 참조 문제 해결하기 위한 어노테이션
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booth_id",nullable = false)
    private Booth booth;

    @JsonBackReference //순환 참조 문제 해결하기 위한 어노테이션
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    private int reportCount = 0;

    public void plusReportCount(){
        reportCount = reportCount +1;
    }

    public void commentUpdate(CommentUpdateDto commentUpdateDto){
        this.content = commentUpdateDto.getContent();
    }
    public void commentUpdate(String reportReason){
        this.content = reportReason;
    }
    public void commentUserUpdate(User user){ this.user = user;}

    @OneToMany(mappedBy = "comment",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Report> reports = new ArrayList<>();

    @Builder
    Comment (String content, User user, Booth booth,LocalDateTime createAt){
        this.content=content;
        this.user = user;
        this.booth= booth;
        this.creatAt = createAt;
    }


}
