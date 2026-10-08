package com.wealth.portfolio.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import com.wealth.portfolio.domain.AccountDto;
import com.wealth.portfolio.domain.CustomerDto;
import com.wealth.portfolio.domain.InvestmentDto;
import com.wealth.portfolio.domain.RiskDto;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AggregationHelperService {
	
	@Value("${portfolio.service.timeout}")
	private long serviceTimeout;

	private final DownstreamDataService downstreamDataService;

	private final AsyncTaskExecutor executor;

	// Explicitly manage circuit breakers
	private final CircuitBreaker customerCB;
	private final CircuitBreaker accountrCB;
	private final CircuitBreaker investmentCB;
	private final CircuitBreaker riskCB;

	public AggregationHelperService(DownstreamDataService downstreamDataService, AsyncTaskExecutor applicationTaskExecutor,
			CircuitBreakerRegistry registry) {
		this.downstreamDataService = downstreamDataService;
		this.executor = applicationTaskExecutor;

		// Match names to your application.props configuration
		this.customerCB = registry.circuitBreaker("customerService");
		this.accountrCB = registry.circuitBreaker("accountService");
		this.investmentCB = registry.circuitBreaker("investmentService");
		this.riskCB = registry.circuitBreaker("riskService");
	}

	
	public CompletableFuture<CustomerDto> getCustomer(String customerId) {
		return CompletableFuture
				.supplyAsync(CircuitBreaker.decorateSupplier(customerCB,
						() -> downstreamDataService.fetchCustomerData(customerId)), executor)
				//.orTimeout(serviceTimeout, TimeUnit.MILLISECONDS)
				.exceptionally(ex -> {
					log.error("Customer service failed or timed out. Triggering fallback.", ex);
					return CustomerDto.fallbackData(customerId);
				});
	}

	public CompletableFuture<List<AccountDto>> getAccounts(String customerId) {
		return CompletableFuture
				.supplyAsync(CircuitBreaker.decorateSupplier(accountrCB,
						() -> downstreamDataService.fetchAccountData(customerId)), executor)
				//.orTimeout(serviceTimeout, TimeUnit.MILLISECONDS)
				.exceptionally(ex -> {
					log.error("Account service failed or timed out. Triggering fallback.", ex);
					return List.of(AccountDto.fallbackData());
				});
	}

	public CompletableFuture<List<InvestmentDto>> getInvestments(String customerId) {
		return CompletableFuture
				.supplyAsync(CircuitBreaker.decorateSupplier(investmentCB,
						() -> downstreamDataService.fetchInvestmentData(customerId)), executor)
				//.orTimeout(serviceTimeout, TimeUnit.MILLISECONDS)
				.exceptionally(ex -> {
					log.error("Investment service failed or timed out. Triggering fallback.", ex);
					return List.of(InvestmentDto.fallbackData());
				});
	}

	public CompletableFuture<RiskDto> getRiskProfile(String customerId) {
		return CompletableFuture.supplyAsync(
				CircuitBreaker.decorateSupplier(riskCB, () -> downstreamDataService.fetchRiskData(customerId)), executor)
				//.orTimeout(serviceTimeout, TimeUnit.MILLISECONDS)
				.exceptionally(ex -> {
					log.error("Risk service failed or timed out. Triggering fallback.", ex);
					return RiskDto.fallbackData();
				});
	}

	public CompletableFuture<Integer> calculatePortfolioValue(CompletableFuture<List<AccountDto>> accountFuture,
			CompletableFuture<List<InvestmentDto>> investmentFuture) {
		return accountFuture.thenCombine(investmentFuture, (accounts, investments) -> {
			int balance = accounts.stream().mapToInt(AccountDto::getBalance).sum();

			int value = investments.stream().mapToInt(InvestmentDto::getValue).sum();

			return balance + value;
		});
	}
	
	/*private TimeLimiter getTimeLimit() {
		return TimeLimiter.of(TimeLimiterConfig.custom()
		        .timeoutDuration(Duration.ofMillis(serviceTimeout))
		        .build());
	}*/
	
}
