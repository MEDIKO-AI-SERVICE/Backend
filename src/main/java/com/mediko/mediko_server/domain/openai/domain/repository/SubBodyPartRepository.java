package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.openai.domain.MainBodyPart;
import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface SubBodyPartRepository extends JpaRepository<SubBodyPart, Long> {
    //주어진 MainBodyPart에 속한 모든 SubBodyPart 조회
    @Query("SELECT sbp FROM SubBodyPart sbp JOIN sbp.mainBodyPart mbp WHERE mbp.body IN :bodies")
    List<SubBodyPart> findAllByMainBodyPartBodies(@Param("bodies") List<String> bodies);

    // 주어진 body와 일치하는 SubBodyPart 조히
    Optional<SubBodyPart> findByBody(String body);
}
