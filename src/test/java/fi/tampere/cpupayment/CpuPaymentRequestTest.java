package fi.tampere.cpupayment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class CpuPaymentRequestTest {


    private static final Logger logger = LoggerFactory.getLogger(CpuPaymentRequestTest.class);

    // Expected: 3.0.5&examplecom&12345&3&new subscription&Charlie Customer&1111&1&100&Product-specific info&1212&150&10&charlie.customer@example.com&Charlie&Customer&https://www.example.com/return-path&https://www.example.com/notification-path&30&202104302359&123
    // Actual:   3.0.5&examplecom&12345&3&new subscription&Charlie Customer&1111&1&100&Product-specific info&1212&150&10&charlie.customer@example.com&Charlie&Customer&https://www.example.com/return-path&https://www.example.com/notification-path&30&202104302359&123
    public void testFromDocumentation() {
        CpuPaymentRequestDTO req = new CpuPaymentRequestDTO("3.0.5", "examplecom", "12345", CpuPaymentAction.NEW_SUBSCRIPTION, "Charlie Customer", "charlie.customer@example.com", "Charlie", "Customer", null, "https://www.example.com/return-path", "https://www.example.com/notification-path", null, 30, "202104302359", null, null);
        req.getProducts().add(new CpuPaymentRequestProductDTO("1111", 1, 100, "Product-specific info", null));
        req.getProducts().add(new CpuPaymentRequestProductDTO("1212", null, 150, null, "10"));
        String checksum = req.calculateChecksum(() -> "123");
        assertEquals(checksum, "0a3bd64c9dbc104f81a9a28cd13cfe1b7efa0be6a4b7d63f33bf78acbb601ef2");
    }

}
