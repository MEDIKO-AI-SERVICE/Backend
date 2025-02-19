package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.DetailedSign;
import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.StreamingHttpOutputMessage;

import java.util.List;
import java.util.Optional;

public interface DetailedSignRepository extends JpaRepository<DetailedSign, Long> {
    //주어진 SubBodyPart에 속한 모든 DetailedSign을 조회
    List<DetailedSign> findBySubBodyPart(SubBodyPart subBodyPart);

    List<DetailedSign> findBySubBodyPartIdIn(List<Long> subBodyPartIds);
}
