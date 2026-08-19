package fi.tampere.cpupayment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.tampere.cpupayment.CpuChecksummable.CpuPaymentSecretProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CpuPaymentClient {

    private static final Logger logger = LoggerFactory.getLogger(CpuPaymentClient.class);
    private static final BigDecimal CENTS = BigDecimal.valueOf(100);

    private final String cpuUrl;
    private final String cpuSource;
    private final String cpuProductCode;
    private final String cpuVatClass;
    private final String developerPrefix;
    private final CpuPaymentSecretProvider cpuSecretProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public CpuPaymentClient(
        String cpuUrl,
        String cpuSource,
        String cpuSecret,
        String cpuProductCode,
        String cpuVatClass,
        String developerPrefix) {
        this.cpuUrl = cpuUrl;
        this.cpuSource = cpuSource;
        this.cpuSecretProvider = () -> cpuSecret;
        this.cpuProductCode = cpuProductCode;
        this.cpuVatClass = cpuVatClass;
        this.developerPrefix = developerPrefix;
    }

    public record PaymentResult(CpuPaymentResponseDTO response, boolean checksumValid) {
    }

    /**
     * Result of parsing a CPU status notification, i.e. an asynchronous callback or a browser return.
     */
    public record NotificationResult(
        Long orderId,
        CpuPaymentStatus status,
        boolean checksumValid,
        String statusMethod,
        String raw,
        List<String> details) {
    }

    public Optional<PaymentResult> createPayment(CpuPaymentOrderDetails details) {
        CpuPaymentRequestDTO request = new CpuPaymentRequestDTO(cpuSource, developerPrefix + details.orderId(), details.description(), details.notificationAddress());
        request.setReturnAddress(details.returnAddress());
        request.setEmail(details.email());
        request.setFirstName(details.firstName());
        request.setLastName(details.lastName());

        details.products().forEach(product -> {
            String description = product.description() == null
                ? null
                : product.description().substring(0, Math.min(product.description().length(), 99));
            request.getProducts().add(new CpuPaymentRequestProductDTO(cpuProductCode, 1, product.price().multiply(CENTS).intValue(), description, cpuVatClass));
        });
        request.addHash(cpuSecretProvider);

        return sendPayment(request).map(response -> new PaymentResult(response, response.verifyChecksum(cpuSecretProvider)));
    }

    /**
     * Parses the asynchronous JSON callback CPU POSTs to the notification address.
     */
    public NotificationResult parseCallback(String rawBody) {
        CpuPaymentCallbackDTO callback;
        try {
            callback = objectMapper.readValue(rawBody, CpuPaymentCallbackDTO.class);
        } catch (JsonProcessingException e) {
            throw new CpuPaymentException("Error parsing CPU payment callback", e);
        }

        List<String> details = callback.payments() == null
            ? List.of()
            : callback.payments().stream().map(payment -> formatCallbackDetail(callback, payment)).toList();

        return toNotificationResult(callback, details);
    }

    /**
     * Parses the query parameters CPU appends when redirecting the customer's browser back to the return address.
     */
    public NotificationResult parseReturn(Map<String, String> queryParams) {
        CpuPaymentReturnDTO paymentReturn = new CpuPaymentReturnDTO(
            queryParams.get("Id"),
            parseIntOrNull(queryParams.get("Status")),
            queryParams.get("Reference"),
            queryParams.get("PaymentMethod"),
            parseIntOrNull(queryParams.get("PaymentSum")),
            queryParams.get("Timestamp"),
            queryParams.get("PaymentDescription"),
            queryParams.get("CardExpiration"),
            queryParams.get("SubscriptionCode"),
            queryParams.get("Hash"));

        return toNotificationResult(paymentReturn, List.of(formatReturnDetail(paymentReturn)));
    }

    private NotificationResult toNotificationResult(CpuPaymentStatusDTO payload, List<String> details) {
        return new NotificationResult(
            payload.getEntityId(developerPrefix),
            payload.status(),
            payload.verifyChecksum(cpuSecretProvider),
            payload.statusMethod(),
            String.valueOf(payload),
            details);
    }

    private static String formatCallbackDetail(CpuPaymentCallbackDTO payment, CpuPaymentCallbackPaymentsDto p) {
        return "payment details: "
            + "source: '" + payment.statusMethod()
            + "' Status: '" + payment.status()
            + "' method: '" + CpuPaymentMethod.getPaymentMethod(p.paymentMethod())
            + "' description: " + p.paymentDescription()
            + "' sum: " + p.paymentSum()
            + "' timestamp: " + p.timestamp();
    }

    private static String formatReturnDetail(CpuPaymentReturnDTO payment) {
        return "payment details: "
            + "source: '" + payment.statusMethod()
            + "' Status: '" + payment.status()
            + "' method: '" + CpuPaymentMethod.getPaymentMethod(payment.paymentMethod())
            + "' description: " + payment.paymentDescription()
            + "' sum: " + payment.paymentSum()
            + "' timestamp: " + payment.timestamp();
    }

    private static Integer parseIntOrNull(String value) {
        return value == null ? null : Integer.valueOf(value);
    }

    private Optional<CpuPaymentResponseDTO> sendPayment(CpuPaymentRequestDTO request) {
        String body;
        try {
            body = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new CpuPaymentException("Error serializing CPU payment request", e);
        }

        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(cpuUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new CpuPaymentException("Error sending payment request to CPU", e);
        }

        if (response.statusCode() != 200) {
            logger.warn("CPU payment request failed with status {}", response.statusCode());
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(response.body(), CpuPaymentResponseDTO.class));
        } catch (JsonProcessingException e) {
            throw new CpuPaymentException("Error parsing response from CPU: " + response.body(), e);
        }
    }
}
