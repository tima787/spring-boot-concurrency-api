package com.wealth.portfolio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.web.client.ResourceAccessException;

import com.wealth.portfolio.domain.AccountDto;
import com.wealth.portfolio.domain.AggregatorResponse;
import com.wealth.portfolio.domain.CustomerDto;
import com.wealth.portfolio.domain.InvestmentDto;
import com.wealth.portfolio.domain.RiskDto;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@ExtendWith(MockitoExtension.class)
public class AggregatorServiceTest {

	@Mock
    private DownstreamDataService dataService;

    private AggregatorService aggregatorService;
    private AggregationHelperService aggregatorHelperService;
    private CircuitBreakerRegistry registry;

    @BeforeEach
    void setUp() {
        // Build an in-memory Resilience4j Registry using defaults
        registry = CircuitBreakerRegistry.ofDefaults();
        
        // Instantiate a real task executor using Java 21 Virtual Threads
        TaskExecutorAdapter virtualThreadExecutor = new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
        aggregatorHelperService = new AggregationHelperService (dataService, virtualThreadExecutor, registry);
        aggregatorService = new AggregatorService(aggregatorHelperService);
    }

    @Test
    void shouldReturnCompleteAggregationResultWhenAllMocksSucceed() {
        // Given
        String customerId = "123";
        CustomerDto customerDto = CustomerDto.fallbackData(customerId);
        AccountDto accountDto = AccountDto.fallbackData();
        InvestmentDto investmentDto = InvestmentDto.fallbackData();
        RiskDto riskDto = RiskDto.fallbackData();

        //when
        when(dataService.fetchCustomerData(customerId)).thenReturn(customerDto);
        when(dataService.fetchAccountData(customerId)).thenReturn(List.of(accountDto));
        when(dataService.fetchInvestmentData(customerId)).thenReturn(List.of(investmentDto));
        when(dataService.fetchRiskData(customerId)).thenReturn(riskDto);

        AggregatorResponse result = aggregatorService.getAggregatedData(customerId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getAccounts().get(0).getAccountId()).isEqualTo("A100");
        assertThat(result.getInvestments().get(0).getInstrument()).isEqualTo("ETF");
        
       
        verify(dataService, times(1)).fetchCustomerData(customerId);
        verify(dataService, times(1)).fetchAccountData(customerId);
        verify(dataService, times(1)).fetchInvestmentData(customerId);
        verify(dataService, times(1)).fetchRiskData(customerId);
    }

	@Test
	void shouldGracefullyRecoverWithPartialFallbacksWhenSpecificServicesFail() {
		// Given
		String customerId = "user-fail";
		CustomerDto customerDto = CustomerDto.fallbackData(customerId);
		AccountDto accountDto = AccountDto.fallbackData();
		when(dataService.fetchCustomerData(customerId)).thenReturn(customerDto);
		when(dataService.fetchAccountData(customerId)).thenReturn(List.of(accountDto));
		when(dataService.fetchInvestmentData(customerId)).thenThrow(new RuntimeException("Database down"));
		when(dataService.fetchRiskData(customerId)).thenThrow(new ResourceAccessException("I/O read timeout reached"));
		AggregatorResponse result = aggregatorService.getAggregatedData(customerId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("John Doe");
	}
}
