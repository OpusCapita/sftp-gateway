package com.opuscapita.tnt.model.properties;

public enum Product {

    INVOICERECEIVING("invoiceReceiving"),
    INVOICESENDING("invoiceSending"),
    B2B_INTEGRATION("b2b-integration"),
    BNA_ARCHIVE_A2A("BNArchiveA2A"),
    IPAINTEGRATION("IPAIntegration"),
    P2P_EXCHANGE("P2P-Exchange"),
    O2C_EXCHANGE("O2C-Exchange");

    public final String product;

    private Product(String _product) {
        this.product = _product;
    }
}
