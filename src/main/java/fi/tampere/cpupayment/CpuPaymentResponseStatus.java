package fi.tampere.cpupayment;

public enum CpuPaymentResponseStatus {
    CANCELLED(0, "Cancelled"),
    SUCCESSFUL(1, "Payment successful"),
    IN_PROGRESS(2, "In progress"),
    UNSUCCESSFUL(98, "Unsuccessful"),
    ERROR(99, "Error");

    private final int code;
    private final String description;

    CpuPaymentResponseStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CpuPaymentResponseStatus fromCode(int code) {
        for (CpuPaymentResponseStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown payment status code: " + code);
    }

    public static boolean isSuccessful(int statusCode, int mode) {
        if (mode == 1) {
            return statusCode == IN_PROGRESS.code;
        } else if (mode == 2) {
            return statusCode == SUCCESSFUL.code;
        }
        return false;
    }
}
