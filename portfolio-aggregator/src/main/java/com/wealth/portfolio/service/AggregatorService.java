package com.wealth.portfolio.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.wealth.portfolio.domain.AccountDto;
import com.wealth.portfolio.domain.AggregatorResponse;
import com.wealth.portfolio.domain.CustomerDto;
import com.wealth.portfolio.domain.InvestmentDto;
import com.wealth.portfolio.domain.RiskDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AggregatorService {

	private final AggregationHelperService aggregationHelperService;


	public AggregatorService(AggregationHelperService aggregationHelperService) {
		this.aggregationHelperService = aggregationHelperService;
	}

	public AggregatorResponse getAggregatedData(String customerId) {
		// 1. Submit to Virtual Threads with explicit timeouts and Circuit Breaker
		// decoration
		CompletableFuture<CustomerDto> customerFuture = aggregationHelperService.getCustomer(customerId);

		CompletableFuture<List<AccountDto>> accountFuture = aggregationHelperService.getAccounts(customerId);

		CompletableFuture<List<InvestmentDto>> investmentFuture = aggregationHelperService.getInvestments(customerId);

		CompletableFuture<RiskDto> riskFuture = aggregationHelperService.getRiskProfile(customerId);

		// Calculate portfolioValue
		CompletableFuture<Integer> portfolioValue = aggregationHelperService.calculatePortfolioValue(accountFuture, investmentFuture);

		// Join barrier orchestration
		CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(customerFuture, accountFuture,
				investmentFuture, riskFuture, portfolioValue);

		try {
			// Blocks safely. Unbinds carrier OS thread execution while virtual thread is
			// waiting.
			combinedFuture.join();
		} catch (Exception e) {
			log.warn("Aggregation lifecycle completed with partial downstream failures or timeouts.", e);
		} 

		// Safely map collected data.
		return new AggregatorResponse(customerFuture.resultNow().getCustomerId(),customerFuture.resultNow().getName(), accountFuture.resultNow(),
				investmentFuture.resultNow(), riskFuture.resultNow().getProfile(), portfolioValue.resultNow());
	}

	

}
