package org.study.rankingsystem.infra.redis.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RedisUserProfile {
	private final String userId;
	private final String nickname;
	private final String profileImageUrl;
	private final String lastPlayedAt;

	@Builder
	public RedisUserProfile(String userId, String nickname, String profileImageUrl, LocalDateTime lastPlayedAt) {
		this.userId = userId;
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
		this.lastPlayedAt = lastPlayedAt.toString();
	}
}
