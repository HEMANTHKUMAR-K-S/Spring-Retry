
package org.demo.springretry.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExceptionRetryService {

    private final RetryTemplate retryTemplate;

    public ExceptionRetryService(
            @Qualifier("exceptionRetryTemplate")
            RetryTemplate retryTemplate) {

        this.retryTemplate = retryTemplate;
    }

    public String testException(String exceptionName) {

        return retryTemplate.execute(context -> {

            System.out.println(
                    exceptionName + " - Attempt: "
                            + (context.getRetryCount() + 1)
            );

            throw createException(exceptionName);
        });
    }

    private RuntimeException createException(String exceptionName) {

        String className =
                "org.demo.springretry.exception." + exceptionName;

        try {
            Class<? extends RuntimeException> exceptionClass =
                    Class.forName(className)
                            .asSubclass(RuntimeException.class);

            return exceptionClass
                    .getConstructor(String.class)
                    .newInstance(exceptionName + " occurred");

        } catch (ReflectiveOperationException | ClassCastException ex) {

            throw new IllegalArgumentException(
                    "Unable to create exception: " + exceptionName,
                    ex
            );
        }
    }
}
