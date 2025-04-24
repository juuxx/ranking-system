package org.study.rankingsystem.infra.redis.service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.study.rankingsystem.infra.redis.dto.RedisUserProfile;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisUserProfileService {

	private final StringRedisTemplate redisTemplate;

	public Map<String, String> getUserProfile(String userId) {
		String key = "user:profile:" + userId;
		Map<Object, Object> raw = redisTemplate.opsForHash().entries(key);
		if (raw == null || raw.isEmpty()) return Map.of();
		return raw.entrySet()
			.stream()
			.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
	}

	public Map<String, Map<String, String>> getUserProfiles(List<String> userIds) {
		Map<String, Map<String, String>> result = new LinkedHashMap<>();

		List<Object> rawResults = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			StringRedisConnection stringConn = (StringRedisConnection) connection;
			userIds.forEach(userId -> {
				String key = "user:profile:" + userId;
				stringConn.hGetAll(key); // 자동 UTF-8 String 처리
			});
			return null;
		});

		for (int i = 0; i < userIds.size(); i++) {
			@SuppressWarnings("unchecked")
			Map<String, String> profile = (Map<String, String>) rawResults.get(i);
			result.put(userIds.get(i), profile);
		}

		return result;
	}

	public void saveUserProfile(RedisUserProfile userProfile) {
		String key = "user:profile:" + userProfile.getUserId();
		Map<String, String> value = Map.of(
			"nickname", userProfile.getNickname(),
			"profileImageUrl", userProfile.getProfileImageUrl(),
			"lastPlayedAt", userProfile.getLastPlayedAt()
		);
		redisTemplate.opsForHash().putAll(key, value);
		redisTemplate.expire(key, Duration.ofMinutes(30));
	}

	public void extendTTL(String userId, Duration duration) {
		String key = "user:profile:" + userId;
		redisTemplate.expire(key, duration);
	}
}
