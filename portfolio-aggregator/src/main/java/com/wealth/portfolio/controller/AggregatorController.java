package com.wealth.portfolio.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wealth.portfolio.domain.AggregatorResponse;
import com.wealth.portfolio.service.AggregatorService;

import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
@RequestMapping("/api")
public class AggregatorController {
	
	
	private AggregatorService aggregatorService;

	public AggregatorController(AggregatorService aggregatorService) {
		this.aggregatorService = aggregatorService;
	}
	
	
	@GetMapping(value="/v1/customers/{customerId}/portfolio", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AggregatorResponse> getAggregatedResponse(@PathVariable String customerId) {
		log.info("Aggregation process started for customerId: {}", customerId);
		AggregatorResponse result = aggregatorService.getAggregatedData(customerId);
		log.info("Aggregation process completed for customerId: {}", customerId);
        return ResponseEntity.ok(result);
	}
	
	
}
