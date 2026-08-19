package fi.tampere.cpupayment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


interface CpuChecksummable {
    static final Logger logger = LoggerFactory.getLogger(CpuChecksummable.class);

    @FunctionalInterface
    interface CpuPaymentSecretProvider {
        String get();
    }

    @JsonIgnore
    String buildChecksumString(CpuPaymentSecretProvider pwdProvider);

    @JsonProperty("Hash")
    String hash();

    final class CpuChecksumException extends RuntimeException {
        public CpuChecksumException(Exception e) {
            super(e);
        }
    }

    @JsonIgnore
    default String calculateChecksum(CpuPaymentSecretProvider pwdProvider) {
        try {
            String dataString = buildChecksumString(pwdProvider);
            return calculateSHA256(dataString);
        } catch (Exception e) {
            logger.warn("Error calculating checksum for: {}", this, e);
            throw new CpuChecksumException(e);
        }
    }

    @JsonIgnore
    default boolean verifyChecksum(CpuPaymentSecretProvider pwdProvider) {

        String calculatedChecksum = null;
        try {
            calculatedChecksum = this.calculateChecksum(pwdProvider);
        } catch (CpuChecksumException e) {
            logger.debug("Error calculating checksum for: {}", this, e);
            return false;
        }
        return calculatedChecksum.equalsIgnoreCase(this.hash());
    }

    /// /////////
    /// Static helper function
    /// ////////

    static String calculateSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating SHA-256 hash", e);
        }
    }

    static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    static void addRequired(List<String> parts, String val) {
        if (val == null) {
            val = "";
        }
        parts.add(val);
    }

    static void addRequiredInt(List<String> parts, Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        parts.add(value.toString());
    }


    static void addIfNonnullInt(List<String> parts, Integer value) {
        if (value != null) {
            addIfNonnull(parts, value.toString());
        }
    }

    static void addIfNonnull(List<String> parts, String value) {
        if (value != null) {
            parts.add(value);
        }
    }

    static void addIfNonnull(List<String> parts, CpuChecksummableObject value) {
        if (value != null) {
            addIfNonnull(parts, value.getValue());
        }
    }
}
