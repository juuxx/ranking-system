package org.study.rankingsystem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.study.rankingsystem.domain.GameRecord;
import org.study.rankingsystem.domain.User;
import org.study.rankingsystem.dto.AddScoreRequest;
import org.study.rankingsystem.dto.AddedScoredResponse;
import org.study.rankingsystem.dto.RankingTop10Response;
import org.study.rankingsystem.infra.redis.dto.RedisUserProfile;
import org.study.rankingsystem.infra.redis.service.RedisRankingService;
import org.study.rankingsystem.infra.redis.service.RedisUserProfileService;
import org.study.rankingsystem.repository.GameRecordRepository;
import org.study.rankingsystem.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameRecordService {

	private final UserRepository userRepository;
	private final GameRecordRepository gameRecordRepository;
	private final RedisRankingService redisRankingService;
	private final RedisUserProfileService redisUserProfileService;
	private final AsyncGameRecordService asyncGameRecordService;


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

	public RankingTop10Response getTop10() {
		String key = "ranking:game:" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		List<ZSetOperations.TypedTuple<String>> topUsers = redisRankingService.getTopRankings(key, 10);

		List<String> userIds = topUsers.stream()
			.map(ZSetOperations.TypedTuple::getValue)
			.toList();

		Map<String, Map<String, String>> userProfiles = redisUserProfileService.getUserProfiles(userIds);

		int rank = 1;
		List<RankingTop10Response.RankingResponse> result = new ArrayList<>();

		for (ZSetOperations.TypedTuple<String> userScore : topUsers) {
			String userId = userScore.getValue();
			Double score = userScore.getScore();

			Map<String, String> profile = userProfiles.getOrDefault(userId, Map.of());

			// fallback to DB if profile missing
			if (profile.isEmpty()) {
				userRepository.findByUserId(userId)
					.ifPresent(user -> {
						this.saveRedisUserProfile(user, user.getGameRecord().getLastPlayedAt());
						// 갱신 후 다시 반영
						profile.put("nickname", user.getNickname());
						profile.put("profileImageUrl", user.getProfileImageUrl());
						profile.put("lastPlayedAt", user.getGameRecord().getLastPlayedAt().toString());
					});
			}

			if (!profile.isEmpty() && score != null) {
				result.add(RankingTop10Response.RankingResponse.builder()
					.rank(rank++)
					.nickname(profile.getOrDefault("nickname", ""))
					.profileImageUrl(profile.getOrDefault("profileImageUrl", ""))
					.totalScore(score.intValue())
					.lastPlayedAt(profile.getOrDefault("lastPlayedAt", ""))
					.build()
				);
			}
		}

		return new RankingTop10Response(result);
	}

	// 4. Redis 프로필 동기화
	private void saveRedisUserProfile(User user, LocalDateTime now) {
		RedisUserProfile userProfile = getRedisUserProfile(user, now);
		redisUserProfileService.saveUserProfile(userProfile);
	}

	private static RedisUserProfile getRedisUserProfile(User user, LocalDateTime now) {
		return RedisUserProfile.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.profileImageUrl(user.getNickname())
			.lastPlayedAt(now)
			.build();
	}


}
