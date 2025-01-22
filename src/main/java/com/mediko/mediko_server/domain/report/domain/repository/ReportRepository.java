package com.mediko.mediko_server.domain.report.domain.repository;

import com.mediko.mediko_server.domain.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
