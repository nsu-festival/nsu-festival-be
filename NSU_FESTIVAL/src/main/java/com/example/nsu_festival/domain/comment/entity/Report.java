package com.example.nsu_festival.domain.comment.entity;

import com.example.nsu_festival.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private String reportReason;

    @JsonBackReference //순환 참조 문제 해결하기 위한 어노테이션
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @JsonBackReference //순환 참조 문제 해결하기 위한 어노테이션
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comment_id",nullable = false)
    private Comment comment;


}
