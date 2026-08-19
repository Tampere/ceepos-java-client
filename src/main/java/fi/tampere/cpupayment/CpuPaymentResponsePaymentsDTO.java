package fi.tampere.cpupayment;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CpuPaymentResponsePaymentsDTO(
        @JsonProperty("PaymentMethod")
        Integer paymentMethod,
        @JsonProperty("PaymentSum")
        Integer paymentSum,
        @JsonProperty("Timestamp")
        String timestamp,
        @JsonProperty("PaymentDescription")
        String paymentDescription,
        @JsonProperty("PaymentPOS")
        Integer paymentPOS) {
}
