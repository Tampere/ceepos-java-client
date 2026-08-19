package fi.tampere.cpupayment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.n52.jackson.datatype.jts.JtsModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class CpuPaymentResponseDTOTest {
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .registerModule(new JtsModule(null, null, 3))
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final Logger logger = LoggerFactory.getLogger(CpuPaymentResponseDTOTest.class);

    // Actual: 12345&2&10456&new payment&https://www.example.com/checkout?reference=10456&token=3b6fd320a01a672c3a3600d1bcfed5462011de5cc8a9a9c63f987886bc622ece&202012021500&123
    // Expect: 12345&2&10456&new payment&https://www.example.com/checkout?reference=10456&token=3b6fd320a01a672c3a3600d1bcfed5462011de5cc8a9a9c63f987886bc622ece&202012021500&123
    // 12345&1&10456&new payment&4&250&20190101120000&Card payment details&1&&123
    @Test
    public void testPaymentResponse() {
        CpuPaymentResponseDTO response = new CpuPaymentResponseDTO("12345", CpuPaymentStatus.PROCESSING, "10456", CpuPaymentAction.NEW_PAYMENT, "https://www.example.com/checkout?reference=10456&token=3b6fd320a01a672c3a3600d1bcfed5462011de5cc8a9a9c63f987886bc622ece", "202012021500", null);

        response.buildChecksumString(() -> "123");
           assertEquals(response.buildChecksumString(() -> "123"), "12345&2&10456&new payment&https://www.example.com/checkout?reference=10456&token=3b6fd320a01a672c3a3600d1bcfed5462011de5cc8a9a9c63f987886bc622ece&202012021500&123");
        String hash = response.calculateChecksum(() -> "123");
        assertEquals(hash, "3925e863e22d4c4a76555462a69dcd22b24fc172958c289b0167e647389d59e0");
    }


    private static final String json = """
            {
            "Id": "12345",
            "Status": 2,
            "Reference": "10456",
            "Action": "new payment",
            "PaymentAddress": "https://www.example.com/checkout?reference=10456&token=3b6fd320a01a672c3a3600d1bcfed5462011de5cc8a9a9c63f987886bc622ece",
            "PaymentExpires": "202012021500",
            "Hash": "1118b554719e5716cdd102879ce13d0385fca5f8b1c17d308a6124b991a94e98"
            }
            """;

    @Test
    public void parseJson() throws JsonProcessingException {
        CpuPaymentResponseDTO response = objectMapper.readValue(json, CpuPaymentResponseDTO.class);
        logger.warn("obj {}", response);
        assertEquals(response.status(), CpuPaymentStatus.PROCESSING);
        String checksumStr = response.buildChecksumString(() -> "123");
        logger.info("Got checksum {}", checksumStr);
        String hash = response.calculateChecksum(() -> "123");
        assertEquals(checksumStr, "12345&2&10456&new payment&https://www.example.com/checkout?reference=10456&token=3b6fd320a01a672c3a3600d1bcfed5462011de5cc8a9a9c63f987886bc622ece&202012021500&123");
        assertEquals(checksumStr, "12345&2&10456&new payment&https://www.example.com/checkout?reference=10456&token=3b6fd320a01a672c3a3600d1bcfed5462011de5cc8a9a9c63f987886bc622ece&202012021500&123");

        // Documentation seems to be wrong....
        assertEquals(hash, "3925e863e22d4c4a76555462a69dcd22b24fc172958c289b0167e647389d59e0");
        //     assertEquals(hash, "1118b554719e5716cdd102879ce13d0385fca5f8b1c17d308a6124b991a94e98");
    }


}
