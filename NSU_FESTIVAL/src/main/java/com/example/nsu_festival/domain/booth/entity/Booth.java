package com.example.nsu_festival.domain.booth.entity;

import com.example.nsu_festival.domain.comment.entity.Comment;
import com.example.nsu_festival.domain.likes.entity.BoothLiked;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "booth")
public class Booth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booth_Id")
    private Long boothId;

    @NonNull
    private String title;

    @NonNull
    private String content;

    private Long countLike;

    private String area;

    @OneToMany(mappedBy = "booth", fetch = FetchType.LAZY)
    @JsonManagedReference  //순환 참조 문제 해결하기 위한 어노테이션
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "booth",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BoothLiked> boothLiked = new ArrayList<>();

    @OneToMany(mappedBy = "booth",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BoothCategory> boothCategories = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booth_image_id")
    @JsonManagedReference
    private BoothImage boothImage;

    @OneToMany(mappedBy = "booth",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Menu> menus = new ArrayList<>();
}
