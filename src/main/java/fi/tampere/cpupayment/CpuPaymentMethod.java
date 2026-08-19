package fi.tampere.cpupayment;

import java.util.Map;

class CpuPaymentMethod {
    public static final Map<String, String> PAYMENT_METHODS = Map.ofEntries(
        Map.entry("30", "Nets"),
        Map.entry("31", "Visa"),
        Map.entry("32", "Master Card"),
        Map.entry("33", "American Express"),
        Map.entry("34", "Diners Club"),
        Map.entry("35", "JCB"),
        Map.entry("36", "Maestro"),
        Map.entry("37", "Nordea"),
        Map.entry("38", "LähiTapiola"),
        Map.entry("39", "Ålandsbanken"),
        Map.entry("40", "Handelsbanken"),
        Map.entry("41", "Danske Bank"),
        Map.entry("42", "S-Pankki"),
        Map.entry("43", "Aktia"),
        Map.entry("44", "POP Pankki"),
        Map.entry("45", "Säästöpankki"),
        Map.entry("46", "SVEA"),
        Map.entry("47", "Klarna"),
        Map.entry("48", "PayPal"),
        Map.entry("49", "Jousto"),
        Map.entry("50", "Paytrail-account"),
        Map.entry("51", "Other electronic payment"),
        Map.entry("52", "MobilePay"),
        Map.entry("53", "Internal billing"),
        Map.entry("54", "External billing"),
        Map.entry("55", "Siirto"),
        Map.entry("56", "Swish"),
        Map.entry("57", "OP – Osuuspankki"),
        Map.entry("58", "Oma Säästöpankki"),
        Map.entry("59", "Collector"),
        Map.entry("60", "Maksuturva invoice"),
        Map.entry("61", "Maksuturva installation payment"),
        Map.entry("62", "Yrityslasku"),
        Map.entry("63", "Resurs Bank"),
        Map.entry("64", "E-Passi"),
        Map.entry("65", "Smartum")
    );

    public static String getPaymentMethod(int paymentMethodId) {
        return getPaymentMethod(Integer.toString(paymentMethodId));
    }

    public static String getPaymentMethod(String paymentMethod) {
        if(paymentMethod == null){
            return "Payment method is null";
        }
        return PAYMENT_METHODS.getOrDefault(paymentMethod, "UNKNOWN PAYMENT METHOD: " + paymentMethod);

    }
}
