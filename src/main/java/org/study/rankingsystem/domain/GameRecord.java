package org.study.rankingsystem.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Setter
	private int totalScore;

	@Setter
	private LocalDateTime lastPlayedAt;

	@Builder
	public GameRecord(User user, int totalScore, LocalDateTime lastPlayedAt) {
		this.user = user;
		this.totalScore = totalScore;
		this.lastPlayedAt = lastPlayedAt;
	}

	public static GameRecord InitRecord(User user, LocalDateTime now) {
		return new GameRecord(user, 0, now);
	}
}
