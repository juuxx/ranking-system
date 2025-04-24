package org.study.rankingsystem.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.study.rankingsystem.domain.GameRecordLogs;
import org.study.rankingsystem.domain.User;
import org.study.rankingsystem.dto.AddScoreRequest;
import org.study.rankingsystem.infra.redis.dto.RedisUserProfile;
import org.study.rankingsystem.infra.redis.service.RedisRankingService;
import org.study.rankingsystem.infra.redis.service.RedisUserProfileService;
import org.study.rankingsystem.repository.GameRecordLogsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncGameRecordService {

	private final GameRecordLogsRepository gameRecordLogsRepository;
	private final RedisRankingService redisRankingService;
	private final RedisUserProfileService redisUserProfileService;

	@Async
	public void saveLogAsync(User user, int score, LocalDateTime now) {
		gameRecordLogsRepository.save(GameRecordLogs.builder()
			.user(user)
			.score(score)
			.playedAt(now)
			.build());
	}

	@Async
	public void syncRedisAsync(AddScoreRequest request,RedisUserProfile profile) {
		try{
			// redis 프로필 저장
			redisUserProfileService.saveUserProfile(profile);
			// Redis ZSET 점수 갱신
			redisRankingService.updateUserScore(request);
		} catch (Exception e) {
			log.error("[Redis 비동기 처리 실패] userId={} | error={}", request.userId(), e.getMessage(), e);
		}
	}
}