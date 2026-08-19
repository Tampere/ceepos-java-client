package fi.tampere.cpupayment;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Payment statuses used in the responses of the web shop.
 */
public enum CpuPaymentStatus {
    /** Payment successful/action complete */
    SUCCESS(1),

    /** Payment creation failed or cancelled */
    FAILED_OR_CANCELLED(0),

    /** Processing of payment in progress */
    PROCESSING(2),

    /** Payment already completed, cannot delete */
    ALREADY_COMPLETED(3),

    /** Payment already deleted */
    ALREADY_DELETED(4),

    /** Payment cannot be processed because authorization of funds failed */
    AUTHORIZATION_FAILED(95),

    /** Payment cannot be processed because card has expired */
    CARD_EXPIRED(96),

    /** Double Id */
    DOUBLE_ID(97),

    /** System error */
    SYSTEM_ERROR(98),

    /** Faulty payment request */
    FAULTY_REQUEST(99);

    private final int value;

    CpuPaymentStatus(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public static CpuPaymentStatus fromValue(int value) {
        for (CpuPaymentStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown payment status: " + value);
    }

    public String getValueString() {
        return String.valueOf(value);
    }
}
