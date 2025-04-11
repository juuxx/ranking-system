package org.study.rankingsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.study.rankingsystem.domain.GameRecordLogs;

public interface GameRecordLogsRepository extends JpaRepository<GameRecordLogs, Long>{
}