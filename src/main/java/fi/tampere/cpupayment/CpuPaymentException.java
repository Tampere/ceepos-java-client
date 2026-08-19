package fi.tampere.cpupayment;

public class CpuPaymentException extends RuntimeException {
    public CpuPaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
