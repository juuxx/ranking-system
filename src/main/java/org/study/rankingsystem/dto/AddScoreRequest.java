package org.study.rankingsystem.dto;

public record AddScoreRequest(String nickname, String userId, String profileImageUrl, int score) {
	/**
	 *   "nickname": "PlayerOne",
	 *   "userId": "khs"
	 *   "profileImageUrl": "https://cdn.example.com/images/player1.png",
	 *   "score": 150
	 */
}
