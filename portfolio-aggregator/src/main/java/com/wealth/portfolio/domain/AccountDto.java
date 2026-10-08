package com.wealth.portfolio.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

	private String accountId;
	
	private int balance;
	
	public static AccountDto fallbackData() {
		return new AccountDto("A100", 50000);
	}
	
}
