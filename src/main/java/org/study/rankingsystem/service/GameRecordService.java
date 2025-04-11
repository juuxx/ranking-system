package org.study.rankingsystem.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.study.rankingsystem.domain.GameRecord;
import org.study.rankingsystem.domain.GameRecordLogs;
import org.study.rankingsystem.domain.User;
import org.study.rankingsystem.dto.AddScoreRequest;
import org.study.rankingsystem.dto.AddedScoredResponse;
import org.study.rankingsystem.repository.GameRecordLogsRepository;
import org.study.rankingsystem.repository.GameRecordRepository;
import org.study.rankingsystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GameRecordService {

	private final UserRepository userRepository;
	private final GameRecordRepository gameRecordRepository;
	private final GameRecordLogsRepository gameRecordLogsRepository;

	public AddedScoredResponse addScore(AddScoreRequest request) {
		User user = userRepository.findByUserId(request.userId())
			.orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다: " + request.userId()));

		GameRecord record = gameRecordRepository.findAll().stream()
			.filter(r -> r.getUser().getUserId().equals(user.getUserId()))
			.findFirst()
			.orElseGet(() -> GameRecord.builder()
				.user(user)
				.totalScore(0)
				.lastPlayedAt(LocalDateTime.now())
				.build());

		// 누적 점수 갱신
		record.setTotalScore(record.getTotalScore() + request.score());
		record.setLastPlayedAt(LocalDateTime.now());
		gameRecordRepository.save(record);

		// 개별 플레이 이력 저장
		GameRecordLogs log = GameRecordLogs.builder()
			.user(user)
			.score(request.score())
			.playedAt(LocalDateTime.now())
			.build();

		gameRecordLogsRepository.save(log);
		return new AddedScoredResponse();
	}
}
