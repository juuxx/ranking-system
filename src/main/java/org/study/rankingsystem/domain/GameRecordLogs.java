package org.study.rankingsystem.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_record_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameRecordLogs {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private int score;
	private LocalDateTime playedAt;

	@Builder
	public GameRecordLogs(User user, int score, LocalDateTime playedAt) {
		this.user = user;
		this.score = score;
		this.playedAt = playedAt;
	}
}
