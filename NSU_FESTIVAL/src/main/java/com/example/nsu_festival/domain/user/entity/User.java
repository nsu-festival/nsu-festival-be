package com.example.nsu_festival.domain.user.entity;

import com.example.nsu_festival.domain.booth.entity.BoothCategory;
import com.example.nsu_festival.domain.comment.entity.Comment;
import com.example.nsu_festival.domain.comment.entity.Report;
import com.example.nsu_festival.domain.likes.entity.BoothLiked;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소셜 장소 + 소셜 id(번호)
    private String userName;

    // 소셜 닉네임
    private String nickName;

    private String email;

    private String role;

    @Builder
    public User(Long id, String userName, String nickName, String email, String role) {
        this.id = id;
        this.userName = userName;
        this.nickName = nickName;
        this.email = email;
        this.role = role;
    }

    public void userUpdate(String nickName, String email) {
        this.nickName = nickName;
        this.email = email;
    }

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BoothLiked> boothLiked = new ArrayList<>();

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Report> reports = new ArrayList<>();

}
