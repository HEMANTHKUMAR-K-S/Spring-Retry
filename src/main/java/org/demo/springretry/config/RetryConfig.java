
package org.demo.springretry.config;

import org.demo.springretry.exception.InventoryUnavaiableException;
import org.demo.springretry.exception.OrderTemporaryException;
import org.demo.springretry.policy.CustomRetryPolicy;
import org.demo.springretry.properties.ExceptionProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.retry.RetryListener;
import org.springframework.core.retry.RetryState;
import org.springframework.core.retry.Retryable;

import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(ExceptionProperties.class)
public class RetryConfig {

    private static final Logger log =
            LoggerFactory.getLogger(RetryConfig.class);

    private static final String EXCEPTION_PACKAGE =
            "org.demo.springretry.exception.";


    @Bean("inventoryRetryTemplate")
    public org.springframework.core.retry.RetryTemplate inventoryRetryTemplate() {

        org.springframework.core.retry.RetryPolicy retryPolicy =
                org.springframework.core.retry.RetryPolicy.builder()
                        .includes(InventoryUnavaiableException.class)
                        .delay(Duration.ofSeconds(2))
                        .maxRetries(5)
                        .build();

        org.springframework.core.retry.RetryTemplate retryTemplate =
                new org.springframework.core.retry.RetryTemplate(retryPolicy);

        retryTemplate.setRetryListener(new RetryListener() {

            @Override
            public void onRetryableExecution(
                    org.springframework.core.retry.RetryPolicy retryPolicy,
                    Retryable<?> retryable,
                    RetryState retryState) {

                if (retryState.isSuccessful()) {
                    log.info("Inventory operation is successful");
                } else {
                    log.info(
                            "Inventory operation failed: {}",
                            retryState.getLastException()
                    );
                }
            }
        });

        return retryTemplate;
    }


    // 2. Order Retry - Spring Framework 7

    @Bean("orderRetryTemplate")
    public org.springframework.core.retry.RetryTemplate orderRetryTemplate() {

        org.springframework.core.retry.RetryPolicy retryPolicy =
                org.springframework.core.retry.RetryPolicy.builder()
                        .includes(OrderTemporaryException.class)
                        .maxRetries(3)
                        .delay(Duration.ofSeconds(2))
                        .build();

        return new org.springframework.core.retry.RetryTemplate(retryPolicy);
    }


    // 3. Exception-specific Retry - Classic Spring Retry

    @Bean("exceptionRetryTemplate")
    public RetryTemplate exceptionRetryTemplate(
            ExceptionProperties properties) {

        Map<Class<? extends Throwable>,
                org.springframework.retry.RetryPolicy> policies =
                new HashMap<>();

        // Build policies once during bean initialization
        properties.getExceptions().forEach((exceptionName, maxAttempts) -> {

            try {
                Class<?> loadedClass =
                        Class.forName(EXCEPTION_PACKAGE + exceptionName);

                if (!Throwable.class.isAssignableFrom(loadedClass)) {
                    throw new IllegalArgumentException(
                            exceptionName + " is not an exception class"
                    );
                }

                Class<? extends Throwable> exceptionClass =
                        loadedClass.asSubclass(Throwable.class);

                if (maxAttempts == null || maxAttempts < 1) {
                    throw new IllegalArgumentException(
                            "Invalid retry count for " + exceptionName
                    );
                }

                policies.put(
                        exceptionClass,
                        new CustomRetryPolicy(maxAttempts)
                );

            } catch (ClassNotFoundException exception) {
                throw new IllegalStateException(
                        "Exception class not found: " + exceptionName,
                        exception
                );
            }
        });

        ExceptionClassifierRetryPolicy classifier =
                new ExceptionClassifierRetryPolicy();

        classifier.setPolicyMap(policies);

        RetryTemplate retryTemplate = new RetryTemplate();

        retryTemplate.setRetryPolicy(classifier);

        FixedBackOffPolicy backOffPolicy =
                new FixedBackOffPolicy();

        backOffPolicy.setBackOffPeriod(1000);

        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }


    // 4. RestClient

    @Bean
    public RestClient restClient(
            @Value("${retry.inventory.api.base-url}") String baseUrl) {

        return RestClient.create(baseUrl);
    }
}
