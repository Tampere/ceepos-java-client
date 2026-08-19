package fi.tampere.cpupayment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

interface CpuPaymentStatusDTO extends CpuChecksummable {
    @JsonProperty("Status")
    CpuPaymentStatus status();

    @JsonProperty("Id")
    String id();

    String statusMethod();

    @JsonIgnore
    default Long getEntityId(String developerPrefix) {
        String id = id();
        if(developerPrefix != null && !developerPrefix.isEmpty()) {
            id = id.replaceFirst(developerPrefix, "");
        }
        return Long.valueOf(id);
    }

}
