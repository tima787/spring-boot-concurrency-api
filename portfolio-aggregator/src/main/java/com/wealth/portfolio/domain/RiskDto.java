package com.wealth.portfolio.domain;

import com.wealth.portfolio.enums.RiskProfile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RiskDto {

	private RiskProfile profile;
	
	public static RiskDto fallbackData() {
		return new RiskDto(RiskProfile.MODERATE);
	}
	
}
