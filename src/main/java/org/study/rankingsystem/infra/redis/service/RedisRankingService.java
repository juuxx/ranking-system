package org.study.rankingsystem.infra.redis.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.study.rankingsystem.domain.enums.RankingPeriod;
import org.study.rankingsystem.dto.AddScoreRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRankingService {
	private final StringRedisTemplate redisTemplate;

	public void updateUserScore(AddScoreRequest request) {
		LocalDate now = LocalDate.now();
		log.info("[{}] score {} -> All RankingPeriods", request.userId(), request.score());
		redisTemplate.executePipelined((RedisConnection connection) -> {
			for (RankingPeriod period : RankingPeriod.values()) {
				String key = period.getKey(now);
				byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
				byte[] userIdBytes = request.userId().getBytes(StandardCharsets.UTF_8);

				// ZINCRBY
				log.debug("🔁 ZINCRBY key = {}, userId = {}, score = {}", key, request.userId(), request.score());
				connection.zIncrBy(keyBytes, request.score(), userIdBytes);

				// TTL 설정
				Duration ttl = period.getTTL();
				if (ttl != null) {
					log.debug("⏳ TTL 설정: key = {}, TTL = {}초", key, ttl.toSeconds());
					connection.expire(keyBytes, ttl.toSeconds());
				}
			}
			return null;
		});
	}

	public List<ZSetOperations.TypedTuple<String>> getTopRankings(String key, int count) {
		Set<ZSetOperations.TypedTuple<String>> raw = redisTemplate.opsForZSet()
			.reverseRangeWithScores(key, 0, count - 1);
		return raw == null ? List.of() : new ArrayList<>(raw);
	}
}