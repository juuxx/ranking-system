package org.study.rankingsystem.infra.redis.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.study.rankingsystem.dto.AddScoreRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisRankingService {
	private final StringRedisTemplate redisTemplate;

	public void updateUserScore(AddScoreRequest request) {
		String todayKey = "ranking:game:" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		String globalKey = "ranking:game:all";

		// 오늘 날짜 기준 랭킹
		redisTemplate.opsForZSet().incrementScore(todayKey, request.userId(), request.score());
		redisTemplate.expire(todayKey, Duration.ofDays(1));

		// 전체 누적 랭킹
		redisTemplate.opsForZSet().incrementScore(globalKey, request.userId(), request.score());
	}

	public List<ZSetOperations.TypedTuple<String>> getTopRankings(String key, int count) {
		Set<ZSetOperations.TypedTuple<String>> raw = redisTemplate.opsForZSet()
			.reverseRangeWithScores(key, 0, count - 1);
		return raw == null ? List.of() : new ArrayList<>(raw);
	}
}