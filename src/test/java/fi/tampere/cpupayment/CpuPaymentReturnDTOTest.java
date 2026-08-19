package fi.tampere.cpupayment;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Test
public class CpuPaymentReturnDTOTest {
    /**
     * Test CpuPaymentReturnDTO checksum calculation with example from documentation (3.3.2)
     * The expected checksum string is: 12345&1&10456&30&9000&202012041200&1234&828282&123
     * Expected hash: 09e01754036dd5b0f78c98f5fde24c91c901fc6c0b87fdc13ca9483372dc270f
     */
    public void testReturnDtoChecksumFromDocumentation() {
        CpuPaymentReturnDTO returnDto = new CpuPaymentReturnDTO(
                "12345",
                CpuPaymentStatus.SUCCESS.getValue(),
                "10456",
                "30",
                9000,
                "202012041200",
                "1234",
                null,
                "828282",
                "09e01754036dd5b0f78c98f5fde24c91c901fc6c0b87fdc13ca9483372dc270f"
        );

        // Verify the checksum string is built correctly
        String checksumString = returnDto.buildChecksumString(() -> "123");
        assertEquals(checksumString, "12345&1&10456&30&9000&202012041200&1234&828282&123");

        // Verify the hash calculation
        String calculatedHash = returnDto.calculateChecksum(() -> "123");
        assertEquals(calculatedHash, "09e01754036dd5b0f78c98f5fde24c91c901fc6c0b87fdc13ca9483372dc270f");

        // Verify checksum validation works
        boolean isValid = returnDto.verifyChecksum(() -> "123");
        assertTrue(isValid);
    }
}
