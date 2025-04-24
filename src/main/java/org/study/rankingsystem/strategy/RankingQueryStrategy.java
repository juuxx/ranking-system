package org.study.rankingsystem.strategy;

import org.study.rankingsystem.dto.RankingTop10Response;

public interface RankingQueryStrategy {
	RankingTop10Response getTop10();
}
