package org.study.rankingsystem.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.study.rankingsystem.domain.GameRecord;
import org.study.rankingsystem.infra.redis.dto.RedisUserProfile;
import org.study.rankingsystem.repository.GameRecordRepository;
import org.study.rankingsystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankingSyncService {

	private final GameRecordRepository gameRecordRepository;
	private final UserRepository userRepository;
	private final StringRedisTemplate redisTemplate;

	public void syncTodayRankingFromDB() {
		String key = "ranking:game:" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		redisTemplate.delete(key);

		List<GameRecord> records = gameRecordRepository.findAll();
		for (GameRecord record : records) {
			redisTemplate.opsForZSet().add(key, record.getUser().getUserId(), record.getTotalScore());
		}

		redisTemplate.expire(key, Duration.ofDays(1));
		System.out.println("✅ ZSET 동기화 완료: " + key);
	}

	public void syncUserProfilesToRedis() {
		List<RedisUserProfile> users = userRepository.getRedisUserProfiles();


		for (RedisUserProfile user : users) {
			String key = "user:profile:" + user.getUserId();
			Map<String, String> value = Map.of(
				"nickname", user.getNickname(),
				"profileImageUrl", user.getProfileImageUrl(),
				"lastPlayedAt", Optional.ofNullable(user.getLastPlayedAt())
					.orElse(LocalDateTime.now().toString())
			);
			redisTemplate.opsForHash().putAll(key, value);
			redisTemplate.expire(key, Duration.ofMinutes(30));
		}
		System.out.println("✅ HASH 프로필 동기화 완료");
	}
}
