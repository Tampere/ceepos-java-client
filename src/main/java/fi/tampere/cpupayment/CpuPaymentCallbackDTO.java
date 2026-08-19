package fi.tampere.cpupayment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static fi.tampere.cpupayment.CpuChecksummable.*;

record CpuPaymentCallbackDTO(

        @JsonProperty("Id")
        String id,

        @JsonProperty("Status")
        CpuPaymentStatus status,

        /**
         * Web shop order number.
         * Payment identifier used internally by the web shop.
         */
        @JsonProperty("Reference")
        String reference,

        @JsonProperty("Payments")
        List<CpuPaymentCallbackPaymentsDto> payments,

        @JsonProperty("SubscriptionCode")
        String subscriptionCode,
        @JsonProperty("LoyaltyCard")
        String loyaltyCard,

        @JsonProperty("Hash")
        String hash) implements CpuPaymentStatusDTO {


    @Override
    public String buildChecksumString(CpuPaymentSecretProvider pwdProvider) {
        List<String> parts = new ArrayList<>();

        // Add parameters in the order specified in documentation
        addRequired(parts, this.id());
        addRequiredInt(parts, this.status().getValue());
        addRequired(parts, this.reference());

        // Add payment details if present
        if (this.payments() != null) {
            for (CpuPaymentCallbackPaymentsDto payment : this.payments) {
                addIfNonnullInt(parts, payment.paymentMethod());
                addIfNonnullInt(parts, payment.paymentSum());
                addIfNonnull(parts, payment.timestamp());
                addIfNonnull(parts, payment.paymentDescription());
                addIfNonnull(parts, payment.cardExpiration());
            }
        }
        addIfNonnull(parts, this.subscriptionCode());
        addIfNonnull(parts, this.loyaltyCard());

        // Add secret key at the end
        parts.add(pwdProvider.get());

        return String.join("&", parts);
    }

    @Override
    public String statusMethod() {
        return "System callback";
    }
}
