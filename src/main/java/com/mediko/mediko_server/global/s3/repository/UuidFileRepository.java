package com.mediko.mediko_server.global.s3.repository;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.Symptom;
import com.mediko.mediko_server.global.s3.UuidFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UuidFileRepository extends JpaRepository<UuidFile, Long> {

    Optional<UuidFile> findByMember(Member member);

    List<UuidFile> findAllBySymptom(Symptom symptom);

    Optional<UuidFile> findByUuid(String uuid);
}
