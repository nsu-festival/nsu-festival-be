package com.example.nsu_festival.global.baseEntity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass   // 하위 엔티티 클래스들이 해당 클래스의 필드들도 컬럼으로 인식
@EntityListeners(value = { AuditingEntityListener.class }) //엔티티의 변경 사항을 자동으로 감지하여 처리
public class BaseTimeRegDateEntity {

    @CreatedDate // 엔티티가 생성될 때 자동으로 시간을 지정
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "createAt", updatable = true) // 컬럼 업데이트시 해당 필드 변경 가능
    private LocalDateTime regDate;
}
