package fi.tampere.cpupayment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.AssertJUnit.*;

@Test
public class CpuPaymentCallbackDTOTest {
    private static final Logger logger = LoggerFactory.getLogger(CpuPaymentCallbackDTOTest.class);

    /**
     * Test CpuPaymentCallbackDTO checksum calculation with example from documentation (3.4.2)
     * The expected checksum string is: 12345&1&10456&30&9000&202012041200&1234&828282&556677&123
     * Expected hash: e4ba5dd934d3519cf99a4b99b88458a0b7855e88324d727c4fc8d6a0700f4aa6
     */
    public void testCallbackDtoChecksumFromDocumentation() {
        CpuPaymentCallbackPaymentsDto payments = new CpuPaymentCallbackPaymentsDto(
                30,
                9000,
                "202012041200",
                "1234",
                null
        );

        CpuPaymentCallbackDTO callback = new CpuPaymentCallbackDTO(
                "12345",
                CpuPaymentStatus.SUCCESS,
                "10456",
                List.of(payments),
                "828282",
                "556677",
                "e4ba5dd934d3519cf99a4b99b88458a0b7855e88324d727c4fc8d6a0700f4aa6"
        );


        // Verify the checksum string is built correctly
        String checksumString = callback.buildChecksumString(() -> "123");
        assertEquals(checksumString, "12345&1&10456&30&9000&202012041200&1234&828282&556677&123");

        // Verify the hash calculation
        String calculatedHash = callback.calculateChecksum(() -> "123");
        assertEquals(calculatedHash, "e4ba5dd934d3519cf99a4b99b88458a0b7855e88324d727c4fc8d6a0700f4aa6");

        // Verify checksum validation works
        boolean isValid = callback.verifyChecksum(() -> "123");
        assertTrue(isValid);
    }

    @Test
    public void testSerialization() throws JsonProcessingException {
        CpuPaymentCallbackPaymentsDto payments = new CpuPaymentCallbackPaymentsDto(
                30,
                9000,
                "202012041200",
                "1234",
                null
        );

        CpuPaymentCallbackDTO callback = new CpuPaymentCallbackDTO(
                "12345",
                CpuPaymentStatus.SUCCESS,
                "10456",
                List.of(payments),
                "828282",
                "556677",
                "e4ba5dd934d3519cf99a4b99b88458a0b7855e88324d727c4fc8d6a0700f4aa6"
        );
        String obj = new ObjectMapper().writeValueAsString(callback);
        logger.warn("obj {}", obj);
    }

    /**
     * Test checksum validation fails with incorrect hash
     */
    public void testCallbackDtoChecksumValidationFails() {
        CpuPaymentCallbackPaymentsDto payments = new CpuPaymentCallbackPaymentsDto(
                30,
                9000,
                "202012041200",
                "1234",
                null
        );

        CpuPaymentCallbackDTO callback = new CpuPaymentCallbackDTO(
                "12345",
                CpuPaymentStatus.SUCCESS,
                "10456",
                List.of(payments),
                "828282",
                "556677",
                "incorrecthash"
        );

        // Verify checksum validation fails
        boolean isValid = callback.verifyChecksum(() -> "123");
        assertFalse(isValid);
    }

    /**
     * Test checksum with optional fields missing
     */
    public void testCallbackDtoChecksumWithOptionalFieldsMissing() {
        CpuPaymentCallbackPaymentsDto payments = new CpuPaymentCallbackPaymentsDto(
                30,
                9000,
                "202012041200",
                "1234",
                null
        );

        CpuPaymentCallbackDTO callback = new CpuPaymentCallbackDTO(
                "12345",
                CpuPaymentStatus.SUCCESS,
                "10456",
                List.of(payments),
                null,  // subscriptionCode is optional
                null,  // loyaltyCard is optional
                null
        );

        // Should not throw exception when optional fields are null
        String checksumString = callback.buildChecksumString(() -> "123");
        assertNotNull(checksumString);

        String calculatedHash = callback.calculateChecksum(() -> "123");
        assertNotNull(calculatedHash);
    }
}
