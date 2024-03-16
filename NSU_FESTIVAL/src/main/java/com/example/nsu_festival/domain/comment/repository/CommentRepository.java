package com.example.nsu_festival.domain.comment.repository;

import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllCommentByBooth(Booth booth);
    Long countCommentByBooth(Booth booth);
}
