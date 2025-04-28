package org.study.rankingsystem.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileRedisDto {
	private String userId;
	private String nickname;
	private String profileImageUrl;
	private int totalScore;
	private LocalDateTime lastPlayedAt;
}
