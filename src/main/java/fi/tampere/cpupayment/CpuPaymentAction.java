package fi.tampere.cpupayment;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CpuPaymentAction implements CpuChecksummableObject {
    NEW_PAYMENT("new payment"),
    NEW_SUBSCRIPTION("new subscription"),
    NEW_SUB_PAYMENT("new subscription payment");

    @JsonValue
    public final String value;

    CpuPaymentAction(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
