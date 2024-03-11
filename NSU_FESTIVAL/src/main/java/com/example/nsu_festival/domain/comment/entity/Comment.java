package com.example.nsu_festival.domain.comment.entity;

import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.comment.dto.CommentUpdateDto;
import com.example.nsu_festival.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

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

    @OneToMany(mappedBy = "comment",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Report> reports = new ArrayList<>();


}
