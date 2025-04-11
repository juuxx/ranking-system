package org.study.rankingsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.study.rankingsystem.domain.GameRecord;

public interface GameRecordRepository extends JpaRepository<GameRecord, Long> {
}