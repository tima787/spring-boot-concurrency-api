package com.wealth.portfolio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.wealth.portfolio.domain.AccountDto;
import com.wealth.portfolio.domain.CustomerDto;
import com.wealth.portfolio.domain.InvestmentDto;
import com.wealth.portfolio.domain.RiskDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@SuppressWarnings(value = { "unchecked" })
public class DownstreamDataService {

	private final RestClient restClient;

	public DownstreamDataService(RestClient restClient) {
		this.restClient = restClient;
	}

	public CustomerDto fetchCustomerData(String customerId) {
		log.info("Customer service fetching data for customerId: {}", customerId);
		return restClient.get().uri("/customer/{id}", customerId).retrieve().body(CustomerDto.class);
	}

	public List<AccountDto> fetchAccountData(String customerId) {
		log.info("Accounts service fetching data for customerId: {}", customerId);
		return (List<AccountDto>) restClient.get().uri("/account?customer={id}", customerId).retrieve()
				.body(AccountDto.class);
	}

	public List<InvestmentDto> fetchInvestmentData(String customerId) {
		log.info("Investment service fetching data for customerId: {}", customerId);
		return (List<InvestmentDto>)restClient.get().uri("/investment/{id}", customerId).retrieve()
				.body(InvestmentDto.class);
	}

	public RiskDto fetchRiskData(String customerId) {
		log.info("Risk service fetching data for customerId: {}", customerId);
		return restClient.get().uri("/risk/profile/{id}", customerId).retrieve().body(RiskDto.class);
	}

}
