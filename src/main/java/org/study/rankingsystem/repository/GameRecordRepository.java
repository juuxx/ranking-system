package org.study.rankingsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.study.rankingsystem.domain.GameRecord;

public interface GameRecordRepository extends JpaRepository<GameRecord, Long> {
	Optional<GameRecord> findByUserId(Long id);
}