package it.iss.accrual.xbrl.dto;

import java.math.BigDecimal;

public class FactXbrl {

    String taxonomy;

    BigDecimal value;

    String decimals;

    String precision;

    String factId;

    ContextXbrl context;

    public FactXbrl(String taxonomy, BigDecimal value, String decimals, String precision, String factId, ContextXbrl context) {
        this.taxonomy = taxonomy;
        this.value = value;
        this.decimals = decimals;
        this.precision = precision;
        this.factId = factId;
        this.context=context;
    }

    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getFactId() {
        return factId;
    }

    public void setFactId(String factId) {
        this.factId = factId;
    }

    public ContextXbrl getContext() {
        return context;
    }

    public void setContext(ContextXbrl context) {
        this.context = context;
    }
}
