package com.example.nsu_festival.domain.notice.repository;

import com.example.nsu_festival.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
