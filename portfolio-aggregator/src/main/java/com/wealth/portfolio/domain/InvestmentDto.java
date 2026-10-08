package com.wealth.portfolio.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentDto {
	
	private String instrument;
	
	private int value;
	
	public static InvestmentDto fallbackData() {
		return new InvestmentDto("ETF", 20000);
	}

}
