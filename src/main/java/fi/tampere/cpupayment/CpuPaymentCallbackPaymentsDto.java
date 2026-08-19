package fi.tampere.cpupayment;

import com.fasterxml.jackson.annotation.JsonProperty;

record CpuPaymentCallbackPaymentsDto(
        @JsonProperty("PaymentMethod")
        Integer paymentMethod,
        @JsonProperty("PaymentSum")
        Integer paymentSum,
        @JsonProperty("Timestamp")

        String timestamp,
        @JsonProperty("PaymentDescription")
        String paymentDescription,
        @JsonProperty("CardExpiration")
        String cardExpiration
) {

}
