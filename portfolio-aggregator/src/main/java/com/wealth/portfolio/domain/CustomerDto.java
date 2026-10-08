package com.wealth.portfolio.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

	private String customerId;
	
	private String name;
	
	public static CustomerDto fallbackData(String custId) {
		return new CustomerDto(custId, "John Doe");
	}
}
