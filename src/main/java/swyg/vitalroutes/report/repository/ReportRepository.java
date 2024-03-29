package swyg.vitalroutes.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyg.vitalroutes.report.domain.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
