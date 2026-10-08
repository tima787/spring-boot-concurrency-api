package com.wealth.portfolio.config;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {

	@Bean(name = "applicationTaskExecutor")
    AsyncTaskExecutor asyncTaskExecutor() {
        // Leverages Java 21 Virtual Threads instead of a pooled platform thread approach
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}
