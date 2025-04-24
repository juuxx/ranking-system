package org.study.rankingsystem.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.study.rankingsystem.domain.GameRecord;
import org.study.rankingsystem.domain.User;
import org.study.rankingsystem.dto.AddScoreRequest;
import org.study.rankingsystem.dto.AddedScoredResponse;
import org.study.rankingsystem.dto.RankingTop10Response;
import org.study.rankingsystem.infra.redis.dto.RedisUserProfile;
import org.study.rankingsystem.repository.GameRecordRepository;
import org.study.rankingsystem.repository.UserRepository;
import org.study.rankingsystem.strategy.RankingQueryStrategy;
import org.study.rankingsystem.strategy.RankingStrategyFactory;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameRecordService {

	private final UserRepository userRepository;
	private final GameRecordRepository gameRecordRepository;
	private final AsyncGameRecordService asyncGameRecordService;
	private final RankingStrategyFactory rankingStrategyFactory;


	@Transactional
	public AddedScoredResponse addScore(AddScoreRequest request) {
		LocalDateTime now = LocalDateTime.now();
		// 1. 점수 DB 저장
		User user = userRepository.findByUserId(request.userId())
			.orElseGet(() -> userRepository.save(request.toUser()));

		user.setNickname(request.nickname()); // 최신화
		user.setProfileImageUrl(request.profileImageUrl());

		userRepository.save(user);

		// 2. 총합 점수 업데이트
		GameRecord record = gameRecordRepository.findByUserId(user.getId())
			.orElseGet(() -> GameRecord.InitRecord(user, now));

		record.setTotalScore(record.getTotalScore() + request.score());
		record.setLastPlayedAt(now);
		gameRecordRepository.save(record);

		// 비동기 호출
		RedisUserProfile userProfile = getRedisUserProfile(user, now);
		asyncGameRecordService.saveLogAsync(user, request.score(), now);
		asyncGameRecordService.syncRedisAsync(request, userProfile);

		return new AddedScoredResponse();
	}

	public RankingTop10Response getTop10(String strategy) {
		RankingQueryStrategy rankingQueryStrategy = rankingStrategyFactory.of(strategy);
		return rankingQueryStrategy.getTop10();
	}

	// 4. Redis 프로필 동기화
	// private void saveRedisUserProfile(User user, LocalDateTime now) {
	// 	RedisUserProfile userProfile = getRedisUserProfile(user, now);
	// 	redisUserProfileService.saveUserProfile(userProfile);
	// }

	private static RedisUserProfile getRedisUserProfile(User user, LocalDateTime now) {
		return RedisUserProfile.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.profileImageUrl(user.getNickname())
			.lastPlayedAt(now)
			.build();
	}


}
