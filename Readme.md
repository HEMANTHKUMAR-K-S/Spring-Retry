# Custom Retry Policy with Spring Retry

## Overview

Implemented a custom exception-specific retry mechanism using the Spring Retry library. The application dynamically identifies exceptions and applies individual retry limits configured in `application.yml`.

The implementation uses `RetryTemplate`, `ExceptionClassifierRetryPolicy`, a policy `Map`, and a custom implementation of the `RetryPolicy` interface.

## Technologies Used

- Java 27
- Spring Boot 4.2.0
- Spring Retry 2.0.13
- Gradle
- YAML Configuration

## Key Features

- Exception-specific retry configuration.
- Dynamic exception creation using Java Reflection.
- Custom retry policy implementation.
- Exception-based retry policy selection.
- Centralized retry configuration.
- Configurable maximum attempts for each exception.
- Removed switch-case logic from the service layer.

## Configuration

Retry limits are maintained in `application.yml`.

```yaml
app:
  retry:
    exceptions:
      InvalidUserException: 3
      ServerUnavailableException: 4
      DatabaseConnectionException: 5
      PaymentFailedException: 3
      ProductNotFoundException: 2
      EmailServiceException: 4
      ExternalApiException: 6
```

The configured values represent total attempts, including the initial execution.

## Architecture

```text
HTTP Request
     |
     v
ExceptionRetryController
     |
     v
ExceptionRetryService
     |
     v
RetryTemplate
     |
     v
ExceptionClassifierRetryPolicy
     |
     v
Policies Map
     |
     v
CustomRetryPolicy
     |
     v
Retry Decision
```

## Implementation Details

### 1. ExceptionProperties

Uses `@ConfigurationProperties` to bind exception-specific retry limits from YAML into a Java Map.

### 2. ExceptionRetryController

Accepts an exception name through an HTTP GET endpoint and delegates the request to the service.

### 3. ExceptionRetryService

Uses `RetryTemplate` to execute the retry operation.

Java Reflection dynamically loads and creates the requested exception, eliminating switch-case statements for individual exception types.

### 4. RetryConfig

Creates the retry policies using the configured exception names and attempt limits.

Maintains a Map containing:

- Key: Exception class.
- Value: Corresponding `CustomRetryPolicy`.

Connects the Map to `ExceptionClassifierRetryPolicy` using `setPolicyMap()`.

### 5. CustomRetryPolicy

Implements Spring Retry's `RetryPolicy` interface.

Overrides the following methods:

| Method | Responsibility |
|---|---|
| `open()` | Creates the retry context. |
| `canRetry()` | Determines whether another attempt is allowed. |
| `registerThrowable()` | Records the exception in the retry context. |
| `close()` | Closes the retry context after execution. |

## Execution Flow

1. The client sends an HTTP request containing an exception name.
2. The controller forwards the exception name to the service.
3. The service invokes `RetryTemplate`.
4. Reflection dynamically creates the requested exception.
5. The exception is thrown during execution.
6. `ExceptionClassifierRetryPolicy` identifies the exception type.
7. The classifier selects the corresponding policy from the Map.
8. `CustomRetryPolicy` checks whether another attempt is allowed.
9. `RetryTemplate` retries the operation until it succeeds or reaches the configured attempt limit.
10. If all attempts fail, the final exception is propagated.

## Testing

Example endpoints:

```http
GET /retry/InvalidUserException
GET /retry/PaymentFailedException
GET /retry/ExternalApiException
```

Expected maximum attempts:

| Exception | Maximum Attempts |
|---|---:|
| InvalidUserException | 3 |
| ServerUnavailableException | 4 |
| DatabaseConnectionException | 5 |
| PaymentFailedException | 3 |
| ProductNotFoundException | 2 |
| EmailServiceException | 4 |
| ExternalApiException | 6 |

## Concepts Learned

- Spring Retry architecture.
- RetryTemplate and RetryContext.
- Custom RetryPolicy implementation.
- ExceptionClassifierRetryPolicy.
- Java Map and key-based policy lookup.
- Java Reflection and dynamic exception creation.
- Retry lifecycle methods.
- Separation of responsibilities.
- Strategy Design Pattern.

## Conclusion

Successfully implemented a configurable and extensible custom retry mechanism using Spring Retry. The architecture separates exception configuration, exception creation, policy selection, and retry decision-making, making it easier to introduce new exception types and customize retry behavior without modifying the service logic.