package fi.tampere.cpupayment;

import static fi.tampere.cpupayment.CpuChecksummable.addIfNonnull;
import static fi.tampere.cpupayment.CpuChecksummable.addIfNonnullInt;
import static fi.tampere.cpupayment.CpuChecksummable.addRequired;
import static fi.tampere.cpupayment.CpuChecksummable.addRequiredInt;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nonnull;

public class CpuPaymentRequestDTO implements CpuChecksummable {

    @JsonProperty("ApiVersion")
    @Nonnull
    private String apiVersion = "3.0.5";

    /**
     * Alphanumeric identifier for the source system.
     */
    @JsonProperty("Source")
    @Nonnull
    private String source;

    /**
     * Alphanumeric identifier for the payment.
     * This is source-system-specific information
     */
    @Nonnull
    @JsonProperty("Id")
    private String id;

    /**
     * The operational mode of the interface.
     * 1 = asynchronic point-of-sale system interface
     * 2 = synchronic point-of-sale system interface
     * 3 = Web interface (ALWAYS THIS )
     */
    @JsonProperty("Mode")
    private final Integer mode = 3;

    /**
     * Action to be performed.
     * - New payment
     * * When creating a new normal payment
     * - New subscription
     * * When creating a new subscription
     * - New subscription payment
     * * When creating a new payment related to a subscription created earlier
     */
    @JsonProperty("Action")
    private CpuPaymentAction action = CpuPaymentAction.NEW_PAYMENT;

    /**
     * Payment-specific free-form description.
     * E.g. customer name + library card number. The description
     * is printed on the receipt as a header before the information
     * on product sales.
     */
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Products")
    private List<CpuPaymentRequestProductDTO> products = new ArrayList<>();

    /**
     * Customer email address.
     * <p>
     * The web shop sends confirmations of payments made to
     * this address. If an email address is not provided as an
     * interface parameter, the web shop will prompt the customer
     * to provide one upon navigation to the payment section.
     */
    @JsonProperty("Email")
    private String email;
    @JsonProperty("FirstName")
    private String firstName;
    @JsonProperty("LastName")
    private String lastName;
    /**
     * The desired language version of the online payment
     * interface.
     * The available language versions depend on the
     * implementation of the online payment interface.
     */
    @JsonProperty("Language")
    private String language;

    /**
     * The return address back to the source system.
     * <p>
     * The web shop directs the customer to this address
     * after a payment has been made or cancelled and
     * includes the order parameters as normal GET
     * parameters. For more on the parameters, see 3.3.1.
     */
    @JsonProperty("ReturnAddress")
    private String returnAddress;
    /**
     * The address of the source system for programme contacts.
     * <p>
     * Using the HTTP POST method, the web shop sends
     * a response in JSON format to this address. Its
     * parameters are described under 3.4.1.
     */
    @JsonProperty("NotificationAddress")
    private String notificationAddress;


    /**
     * Required when action is “new subscription payment”.
     */
    @JsonProperty("SubscriptionCode")
    private String subscriptionCode;
    /**
     * The expected period in days for recurring payments
     * related to this subscription.
     * Required when action is “new subscription”.
     */
    @JsonProperty("SubscriptionPeriod")
    private Integer subscriptionPeriod;
    /**
     * Datetime when the subscription ends - no payments
     * can be done after this. Can be left empty, which
     * means that the recurring payment is valid and can be
     * used until the payer's credit card expiry date. If
     * payer's credit card expires, the payments will fail so
     * submitting larger value than that has the same effect
     * than leaving this empty
     */
    @JsonProperty("SubscriptionEnd")
    private String subscriptionEnd;
    /**
     * Preselected payment method, i.e. “paytrail”,
     * “verifone”, “epassi”, “smartum”, “external_billing”.
     * When specified, the user is redirected to the payment
     * provider immediately, if possible. If any of the fields
     * “Firstname”, “Lastname” or “Email” are empty,
     * automatic payment redirection may fail.
     */
    @JsonProperty("PaymentMethod")
    private String paymentMethod;

    /**
     * Additional payment method -related information, like
     * billing customer number.
     */
    @JsonProperty("PaymentInfo")
    private String paymentInfo;
    @JsonProperty("Hash")
    private String hash;


    public CpuPaymentRequestDTO(@Nonnull String source, @Nonnull String id, String description, String notificationAddress) {
        this.source = source;
        this.id = id;
        this.description = description;
        this.notificationAddress = notificationAddress;
    }

    public CpuPaymentRequestDTO(
            @Nonnull String apiVersion,
            @Nonnull String source,
            @Nonnull String id,
            CpuPaymentAction action,
            String description,
            String email,
            String firstName,
            String lastName,
            String language,
            String returnAddress,
            String notificationAddress,
            String subscriptionCode,
            Integer subscriptionPeriod,
            String subscriptionEnd,
            String paymentMethod,
            String paymentInfo) {
        this.apiVersion = apiVersion;
        this.source = source;
        this.id = id;
        this.action = action;
        this.description = description;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.language = language;
        this.returnAddress = returnAddress;
        this.notificationAddress = notificationAddress;
        this.subscriptionCode = subscriptionCode;
        this.subscriptionPeriod = subscriptionPeriod;
        this.subscriptionEnd = subscriptionEnd;
        this.paymentMethod = paymentMethod;
        this.paymentInfo = paymentInfo;
    }

    /**
     * Build the checksum string from payment request parameters
     * Parameters must be in the exact order as specified in the documentation
     */
    @Override
    public String buildChecksumString(CpuPaymentSecretProvider pwdProvider) {
        List<String> parts = new ArrayList<>();

        // Add parameters in the order specified in documentation
        addRequired(parts, this.getApiVersion());
        addRequired(parts, this.getSource());
        addRequired(parts, this.getId());
        addRequiredInt(parts, this.getMode());
        addRequired(parts, this.getAction().value);
        addIfNonnull(parts, this.getDescription());

        // Add product information
        if (this.getProducts() != null) {
            for (CpuPaymentRequestProductDTO product : this.getProducts()) {
                addRequired(parts, product.code());
                addIfNonnullInt(parts, product.amount());
                addIfNonnullInt(parts, product.price());
                addIfNonnull(parts, product.description());
                addIfNonnull(parts, product.taxcode());
            }
        }
        addIfNonnull(parts, this.getEmail());
        addIfNonnull(parts, this.getFirstName());
        addIfNonnull(parts, this.getLastName());
        addIfNonnull(parts, this.getLanguage());


        addRequired(parts, this.getReturnAddress());
        addRequired(parts, this.getNotificationAddress());

        addIfNonnull(parts, this.getSubscriptionCode());
        addIfNonnullInt(parts, this.getSubscriptionPeriod());
        addIfNonnull(parts, this.getSubscriptionEnd());

        addIfNonnull(parts, this.getPaymentMethod());
        addIfNonnull(parts, this.getPaymentInfo());

        // Add secret key at the
        parts.add(pwdProvider.get());

        return String.join("&", parts);
    }

    @Override
    public String hash() {
        return hash;
    }


    @Nonnull
    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(@Nonnull String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Nonnull
    public String getSource() {
        return source;
    }

    public void setSource(@Nonnull String source) {
        this.source = source;
    }

    @Nonnull
    public String getId() {
        return id;
    }

    public void setId(@Nonnull String id) {
        this.id = id;
    }

    public Integer getMode() {
        return mode;
    }

    public CpuPaymentAction getAction() {
        return action;
    }

    public void setAction(CpuPaymentAction action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CpuPaymentRequestProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<CpuPaymentRequestProductDTO> products) {
        this.products = products;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getReturnAddress() {
        return returnAddress;
    }

    public void setReturnAddress(String returnAddress) {
        this.returnAddress = returnAddress;
    }

    public String getNotificationAddress() {
        return notificationAddress;
    }

    public void setNotificationAddress(String notificationAddress) {
        this.notificationAddress = notificationAddress;
    }

    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    public Integer getSubscriptionPeriod() {
        return subscriptionPeriod;
    }

    public void setSubscriptionPeriod(Integer subscriptionPeriod) {
        this.subscriptionPeriod = subscriptionPeriod;
    }

    public String getSubscriptionEnd() {
        return subscriptionEnd;
    }

    public void setSubscriptionEnd(String subscriptionEnd) {
        this.subscriptionEnd = subscriptionEnd;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void addHash(CpuPaymentSecretProvider pwdProvider) {
        this.hash = calculateChecksum(pwdProvider);
    }


}
