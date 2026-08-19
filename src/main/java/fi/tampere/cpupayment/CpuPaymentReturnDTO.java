package fi.tampere.cpupayment;

import java.util.ArrayList;
import java.util.List;

import static fi.tampere.cpupayment.CpuChecksummable.*;

record CpuPaymentReturnDTO(

        String id,

        Integer statusInt,

        /**
         * Web shop order number.
         * Payment identifier used internally by the web shop.
         */
        String reference,

        String paymentMethod,
        Integer paymentSum,
        String timestamp,
        String paymentDescription,
        String cardExpiration,

        String subscriptionCode,

        String hash
) implements CpuPaymentStatusDTO {
    @Override
    public CpuPaymentStatus status() {
        return CpuPaymentStatus.fromValue(statusInt);
    }

    @Override
    public String statusMethod() {
        return "User return";
    }

    /**
     * Build the checksum string from payment return parameters
     * Parameters must be in the exact order as specified in the documentation
     */
    @Override
    public String buildChecksumString(CpuPaymentSecretProvider secretProvider) {
        List<String> parts = new ArrayList<>();

        // Add parameters in the order specified in documentation
        addRequired(parts, this.id());
        addRequired(parts, this.status().getValueString());
        addRequired(parts, this.reference());
        addIfNonnull(parts, this.paymentMethod());
        addIfNonnullInt(parts, this.paymentSum());
        addIfNonnull(parts, this.timestamp());
        addIfNonnull(parts, this.paymentDescription());
        addIfNonnull(parts, this.cardExpiration());
        addIfNonnull(parts, this.subscriptionCode());
        parts.add(secretProvider.get());
        return String.join("&", parts);
    }


}
