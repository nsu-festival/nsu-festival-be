package com.example.nsu_festival.domain.visitor.repository;

import com.example.nsu_festival.domain.visitor.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Optional<Visitor> findByUUID(String UUID);

    @Query("select count(*) from Visitor v where v.visitTime = :now")
    Long countByVisitTime(@Param("now")LocalDate now);

    boolean existsByUUID(String token);
}
