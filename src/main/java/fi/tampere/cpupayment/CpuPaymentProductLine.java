package fi.tampere.cpupayment;

import java.math.BigDecimal;

public record CpuPaymentProductLine(BigDecimal price, String description) {
}
