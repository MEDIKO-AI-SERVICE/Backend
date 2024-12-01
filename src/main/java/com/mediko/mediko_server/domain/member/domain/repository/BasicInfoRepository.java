package com.mediko.mediko_server.domain.member.domain.repository;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BasicInfoRepository extends JpaRepository<BasicInfo, Long> {

}