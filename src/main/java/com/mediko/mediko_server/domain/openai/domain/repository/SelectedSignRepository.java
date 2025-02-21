package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.SelectedSign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SelectedSignRepository extends JpaRepository<SelectedSign, Long> {

    Optional<SelectedSign> findByIdAndMember(Long id, Member member);
}
