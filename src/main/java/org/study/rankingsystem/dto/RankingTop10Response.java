package org.study.rankingsystem.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class RankingTop10Response {

	private List<RankingResponse> top10;

	public RankingTop10Response(List<RankingResponse> ranker) {
		this.top10 = ranker;
	}

	@Builder
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RankingResponse {
		private int rank;
		private String nickname;
		private String profileImageUrl;
		private int totalScore;
		private String lastPlayedAt;
	}
}
