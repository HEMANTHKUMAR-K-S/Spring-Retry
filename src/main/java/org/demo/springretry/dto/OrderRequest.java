
package org.demo.springretry.dto;

import java.math.BigDecimal;

public record OrderRequest(
        String productName,
        Integer quantity,
        BigDecimal amount
) {
}
