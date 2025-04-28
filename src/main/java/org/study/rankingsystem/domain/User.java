package org.study.rankingsystem.domain;

import java.util.List;

import org.springframework.util.ObjectUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String userId;

	// @Setter
	private String nickname;

	// @Setter
	private String profileImageUrl;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private GameRecord gameRecord;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GameRecordLogs> gameRecordLogs;

	public void setNickname(String nickname) {
		if (!ObjectUtils.isEmpty(nickname)) {
			this.nickname = nickname;
		}
	}

	public void setProfileImageUrl(String profileImageUrl) {
		if (!ObjectUtils.isEmpty(profileImageUrl)) {
			this.profileImageUrl = profileImageUrl;
		}
	}

	@Builder
	public User(String userId, String nickname, String profileImageUrl) {
		this.userId = userId;
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
	}
}
