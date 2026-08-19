package fi.tampere.cpupayment;

import java.util.List;

public record CpuPaymentOrderDetails(
        String orderId,
        String description,
        String email,
        String firstName,
        String lastName,
        String returnAddress,
        String notificationAddress,
        List<CpuPaymentProductLine> products
) {
}
