package org.study.rankingsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.study.rankingsystem.domain.GameRecord;
import org.study.rankingsystem.dto.UserProfileRedisDto;

public interface GameRecordRepository extends JpaRepository<GameRecord, Long> {
	Optional<GameRecord> findByUserId(Long id);

	@Query("SELECT new org.study.rankingsystem.dto.UserProfileRedisDto( " +
		"u.userId, u.nickname, u.profileImageUrl, gr.totalScore, gr.lastPlayedAt) " +
		"FROM GameRecord gr " +
		"JOIN gr.user u " +
		"ORDER BY gr.totalScore DESC")
	List<UserProfileRedisDto> findTop10ByOrderByTotalScoreDesc();
}