package com.mediko.mediko_server.global.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)  //이벤트가 발생하면 특정 동작을 진행하는 어노테이션
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate               //Entity가 생성되어 저장될 때 시간이 자동으로 저장 (로컬 기준)
    @Column(updatable = false)
    private LocalDateTime created_at;

    @LastModifiedDate          //Entity의 값을 변경할 때 시간이 자동으로 저장
    private LocalDateTime updated_at;
}
