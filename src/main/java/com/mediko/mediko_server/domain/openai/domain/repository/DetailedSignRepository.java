package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.openai.domain.DetailedSign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailedSignRepository extends JpaRepository<DetailedSign, Long> {

    List<DetailedSign> findBySubBodyPartDescription(String description);

    List<DetailedSign> findBySubBodyPartIdIn(List<Long> subBodyPartIds);
}
