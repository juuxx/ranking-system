package org.study.rankingsystem.strategy;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankingStrategyFactory {

	private final Map<String, RankingQueryStrategy> strategies;


	private static final Map<String, String> STRATEGY_ALIAS = Map.of(
		"redis", "redisRankingStrategy",
		"db", "dbRankingStrategy"
	);

	public RankingQueryStrategy of(String type) {
		String alias = STRATEGY_ALIAS.get(type);
		String beanName = RankingStrategyType.of(alias).getBeanName();
		return strategies.get(beanName);
	}
}
