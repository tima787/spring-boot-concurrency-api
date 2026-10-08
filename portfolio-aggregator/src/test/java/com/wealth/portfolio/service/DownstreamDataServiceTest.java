package com.wealth.portfolio.service;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.wealth.portfolio.domain.CustomerDto;

@WireMockTest
public class DownstreamDataServiceTest {

	private DownstreamDataService downstreamDataService;

	@BeforeEach
	void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
		ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
				.withConnectTimeout(Duration.ofMillis(500)).withReadTimeout(Duration.ofMillis(2000));

		ClientHttpRequestFactory factory = ClientHttpRequestFactoryBuilder.detect().build(settings);

		RestClient restClient = RestClient.builder().requestFactory(factory).baseUrl(wmRuntimeInfo.getHttpBaseUrl())
				.build();

		downstreamDataService = new DownstreamDataService(restClient);
	}

	@Test
	void shouldSuccessfullyFetchUserWhenDownstreamIsHealthy() {
		//given
		String customerObj = """ 
				     { 
						"customerId": "123", "name":"John"
					  }
				    	""";
		// when
		stubFor(get(urlEqualTo("/customer/123")).willReturn(okJson(customerObj)));

		CustomerDto result = downstreamDataService.fetchCustomerData("123");

		// then
		assertThat(result).isNotNull();
		assertThat(result.getCustomerId()).isEqualTo("123");
		assertThat(result.getName()).isEqualTo("John");
	}

	@Test
	void shouldThrowResourceAccessExceptionWhenDownstreamBreachesSocketTimeout() {
		// given
		String accountObj = """ 
			     { 
					"accountId": "123", "balance":"10000"
				  }
			    	""";
		//when
		stubFor(get(urlEqualTo("/account?customer=123"))
				.willReturn(okJson(accountObj).withFixedDelay(3000)));

		//then
		assertThatThrownBy(() -> downstreamDataService.fetchAccountData("123"))
				.isInstanceOf(ResourceAccessException.class);
	}
}
