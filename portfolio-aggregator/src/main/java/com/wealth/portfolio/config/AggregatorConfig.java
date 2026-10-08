package com.wealth.portfolio.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AggregatorConfig {
	
	@Value("${portfolio.rest.client.connect.timeout}")
	private long connectTimeout;

	
	@Value("${portfolio.rest.client.read.timeout}")
	private long readTimeout;


	@Bean
	RestClient restClient(RestClient.Builder builder) {
		ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
				.withConnectTimeout(Duration.ofMillis(connectTimeout)).withReadTimeout(Duration.ofMillis(readTimeout));

		ClientHttpRequestFactory factory = ClientHttpRequestFactoryBuilder.detect().build(settings);

		return builder.requestFactory(factory).baseUrl("https://internal-mesh.local").build();
	}
}
