package org.study.rankingsystem.strategy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.study.rankingsystem.domain.GameRecord;
import org.study.rankingsystem.dto.RankingTop10Response;
import org.study.rankingsystem.repository.GameRecordRepository;

import lombok.RequiredArgsConstructor;

@Service("dbRankingStrategy")
@RequiredArgsConstructor
public class DbRankingQueryStrategy implements RankingQueryStrategy {

	private final GameRecordRepository gameRecordRepository;

	@Override
	public RankingTop10Response getTop10() {
		List<GameRecord> records = gameRecordRepository.findTop10ByOrderByTotalScoreDesc();

		List<RankingTop10Response.RankingResponse> rankings = new ArrayList<>();
		int rank = 1;
		for (GameRecord r : records) {
			rankings.add(RankingTop10Response.RankingResponse.builder()
				.rank(rank++)
				.nickname(r.getUser().getNickname())
				.profileImageUrl(r.getUser().getProfileImageUrl())
				.totalScore(r.getTotalScore())
				.lastPlayedAt(r.getLastPlayedAt().toString())
				.build());
		}

		return new RankingTop10Response(rankings);
	}
}
