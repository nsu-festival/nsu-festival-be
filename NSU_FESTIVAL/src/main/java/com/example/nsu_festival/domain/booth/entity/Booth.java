package com.example.nsu_festival.domain.booth.entity;

import com.example.nsu_festival.domain.comment.entity.Comment;
import com.example.nsu_festival.domain.likes.entity.BoothLiked;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.awt.*;
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
    private String a;
//    public void updateCountLike(int countLike){
//        this.countLike = countLike;
//    }
}