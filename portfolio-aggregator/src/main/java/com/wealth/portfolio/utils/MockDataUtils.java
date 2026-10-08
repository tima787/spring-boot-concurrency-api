package com.wealth.portfolio.utils;

import java.util.List;

import com.wealth.portfolio.domain.AccountDto;
import com.wealth.portfolio.domain.CustomerDto;
import com.wealth.portfolio.domain.InvestmentDto;
import com.wealth.portfolio.domain.RiskDto;
import com.wealth.portfolio.enums.RiskProfile;

public class MockDataUtils {

	
	public static CustomerDto mockCustomerData(String customerId) {
		return new CustomerDto(customerId, "Ram");
	}

	public static List<AccountDto> mockAccountData() {
		return List.of(new AccountDto("A100",25000));
	}

	public static List<InvestmentDto> mockInvestmentData() {
		return List.of(new InvestmentDto("FD",10000));
	}

	public static RiskDto mockRiskData() {
		return new RiskDto(RiskProfile.HIGH);
	}

	
}
