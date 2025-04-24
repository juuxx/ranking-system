package org.study.rankingsystem.strategy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RankingStrategyType {
	REDIS("redisRankingStrategy"),
	DB("dbRankingStrategy");

	private final String beanName;

	public static RankingStrategyType of(String beanName) {
		for (RankingStrategyType type : values()) {
			if (type.getBeanName().equalsIgnoreCase(beanName)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown beanName: " + beanName);
	}

}

