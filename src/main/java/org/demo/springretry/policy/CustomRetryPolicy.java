
package org.demo.springretry.policy;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;

public class CustomRetryPolicy implements RetryPolicy {

    private final int maxAttempts;

    public CustomRetryPolicy(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    @Override
    public boolean canRetry(RetryContext context) {
        return context.getRetryCount() < maxAttempts;
    }

    @Override
    public RetryContext open(RetryContext parent) {
        return new org.springframework.retry.context
                .RetryContextSupport(parent);
    }

    @Override
    public void registerThrowable(
            RetryContext context,
            Throwable throwable) {

        ((org.springframework.retry.context.RetryContextSupport)
                context).registerThrowable(throwable);
    }

    @Override
    public void close(RetryContext context) {
    }
}
