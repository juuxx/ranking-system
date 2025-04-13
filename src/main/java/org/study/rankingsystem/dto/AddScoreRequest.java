package org.study.rankingsystem.dto;

import org.study.rankingsystem.domain.User;

public record AddScoreRequest(String nickname,
							  String userId,
							  String profileImageUrl,
							  int score
) {
	public User toUser() {
		return User.builder()
			.userId(userId)
			.nickname(nickname)
			.profileImageUrl(profileImageUrl)
			.build();
	}
}
