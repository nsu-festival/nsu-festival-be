package com.example.nsu_festival.domain.comment.repository;

import com.example.nsu_festival.domain.comment.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
