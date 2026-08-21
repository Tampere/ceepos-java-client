package fi.tampere.cpupayment;

import java.math.BigDecimal;

public record CpuPaymentProductLine(String code, BigDecimal price, String description) {
}
