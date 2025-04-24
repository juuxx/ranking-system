package org.study.rankingsystem.domain.enums;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;

import lombok.Getter;

@Getter
public enum RankingPeriod {

	DAILY {
		@Override
		public String getKey(LocalDate date) {
			return  "ranking:game:" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		}

		@Override
		public Duration getTTL() {
			return Duration.ofDays(1);
		}
	}, WEEKLY {
		@Override
		public String getKey(LocalDate date) {
			int week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
			int year = date.get(IsoFields.WEEK_BASED_YEAR);
			return "ranking:game:week:%dW%02d".formatted(year, week);
		}

		@Override
		public Duration getTTL() {
			return Duration.ofDays(7);
		}
	}, MONTHLY {
		@Override
		public String getKey(LocalDate date) {
			return "ranking:game:month:" + date.format(DateTimeFormatter.ofPattern("yyyyMM"));
		}

		@Override
		public Duration getTTL() {
			return Duration.ofDays(32);
		}
	}, ALL {
		@Override
		public String getKey(LocalDate date) {
			return "ranking:game:all";
		}

		@Override
		public Duration getTTL() {
			return null;
		}
	};

	public abstract String getKey(LocalDate date);
	public abstract Duration getTTL();

}
