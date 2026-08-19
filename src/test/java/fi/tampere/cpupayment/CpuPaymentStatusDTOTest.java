package fi.tampere.cpupayment;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class CpuPaymentStatusDTOTest {

    public static class CpuPaymentStatusDTOTestobj implements CpuPaymentStatusDTO {
        private final String id;

        public CpuPaymentStatusDTOTestobj(String id) {
            this.id = id;
        }

        @Override
        public CpuPaymentStatus status() {
            return null;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public String statusMethod() {
            return "Test method";
        }

        @Override
        public String buildChecksumString(CpuPaymentSecretProvider pwdProvider) {
            return "";
        }

        @Override
        public String hash() {
            return "";
        }
    }

    @Test
    public void testDeveloperPrefix() {
        assertEquals(new CpuPaymentStatusDTOTestobj("1234").getEntityId(null), 1234L);
        assertEquals(new CpuPaymentStatusDTOTestobj("1234").getEntityId("foobar"), 1234L);
        assertEquals(new CpuPaymentStatusDTOTestobj("Testi1234").getEntityId("Testi"), 1234L);

    }

}
