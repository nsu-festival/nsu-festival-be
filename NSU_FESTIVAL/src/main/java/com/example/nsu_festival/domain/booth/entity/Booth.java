package com.example.nsu_festival.domain.booth.entity;

import com.example.nsu_festival.domain.booth.dto.BoothDetailsRequestDto;
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

    @Column(name = "content", length = 1000)
    private String content;

    private int countLike;

    private String area;

    private String boothImageUrl;
    private String entryFee;
    private String boothName;

    @OneToMany(mappedBy = "booth", fetch = FetchType.LAZY)
    @JsonManagedReference  //순환 참조 문제 해결하기 위한 어노테이션
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "booth",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BoothLiked> boothLiked = new ArrayList<>();

    @OneToMany(mappedBy = "booth",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BoothCategory> boothCategories = new ArrayList<>();

    @OneToMany(mappedBy = "booth",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Menu> menus = new ArrayList<>();

    public void setBoothCategories(List<BoothCategory> boothCategories){
        this.boothCategories=boothCategories;
    }
    public void setMenus(List<Menu> menus){this.menus = menus;}
  
    public void updateCountLike(int countLike){
         this.countLike = countLike;
   }

    public void updateBoothDetails(BoothDetailsRequestDto requestDto){
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.area = requestDto.getArea();
        this.entryFee = requestDto.getEntryFee();
        this.boothName = requestDto.getBoothName();
    }

    public void updateBoothCountLike(int countLike){
        this.countLike = countLike;
    }

}
