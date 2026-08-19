package fi.tampere.cpupayment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static fi.tampere.cpupayment.CpuChecksummable.addRequired;
import static fi.tampere.cpupayment.CpuChecksummable.addRequiredInt;

public record CpuPaymentResponseDTO(

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

        @JsonProperty("Action")
        CpuPaymentAction action,

        @JsonProperty("PaymentAddress")
        String paymentAddress,

        @JsonProperty("PaymentExpires")
        String paymentExpires,

        @JsonProperty("Hash")
        String hash) implements CpuPaymentStatusDTO, CpuChecksummable {


    @Override
    public String buildChecksumString(CpuPaymentSecretProvider pwdProvider) {
        List<String> parts = new ArrayList<>();

        // Add parameters in the order specified in documentation (2.3.1.1)
        addRequired(parts, this.id());
        addRequiredInt(parts, this.status().getValue());
        addRequired(parts, this.reference());
        addRequired(parts, this.action().value);
        addRequired(parts, this.paymentAddress());
        addRequired(parts, this.paymentExpires());
        // Add secret key at the end
        parts.add(pwdProvider.get());

        return String.join("&", parts);
    }

    @Override
    public String statusMethod() {
        return "Creation response";
    }
}
