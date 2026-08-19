package fi.tampere.cpupayment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;

public record CpuPaymentRequestProductDTO(

        /**
         * Alphanumeric product code in the point-of-sale system.
         *
         * Ties the payment to an existing product in the point-of-sale
         * system. Among other things, the product name, tax rate
         * and posting used in bookkeeping are determined
         * automatically in the point-of-sale system by this code.
         */
        @Nonnull
        @JsonProperty("Code")
        String code,
        /**
         * Number of products (default 1).
         * For refunds, a negative value must be used.
         */
        @JsonProperty("Amount")
        Integer amount,
        /**
         * Unit price in cents including tax.
         *
         * If this is not entered, the point-of-sale system uses the
         * default price of the product.
         */
        @JsonProperty("Price")
        Integer price,
        /**
         * Free-form description of product sale.
         * This description is printed on the receipt in connection with
         * product name and price. It can also be reported in the
         * product sales reports of the point-of-sale system
         */
        @JsonProperty("Description")
        String description,
        /**
         * The tax rate code of the point-of-sale system.
         *
         * Among other things, this determines the VAT rate and the
         * tax account used in bookkeeping. Must be entered in
         * situations where the tax rate for the sales is different than
         * the point-of-sale system's default tax rate for the product.
         */
        @JsonProperty("Taxcode")
        String taxcode
) {
}
