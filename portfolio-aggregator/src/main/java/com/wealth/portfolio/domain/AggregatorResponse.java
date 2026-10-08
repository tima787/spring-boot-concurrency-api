package com.wealth.portfolio.domain;

import java.util.ArrayList;
import java.util.List;

import com.wealth.portfolio.enums.RiskProfile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregatorResponse {

	private String customerId;
	
	private String name;
	
	private List<AccountDto> accounts  = new ArrayList<>();
	
	private List<InvestmentDto> investments = new ArrayList<>();
	
	private RiskProfile riskProfile;
	
	private Integer portfolioValue;
}
