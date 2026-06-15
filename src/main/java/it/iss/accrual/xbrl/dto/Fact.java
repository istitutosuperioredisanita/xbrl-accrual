package it.iss.accrual.xbrl.dto;

import java.math.BigDecimal;

public class Fact {

    String taxonomy;

    BigDecimal value;

    String decimals;

    String precision;

    String factId;

    public Fact(String taxonomy, BigDecimal value, String decimals, String precision, String factId) {
        this.taxonomy = taxonomy;
        this.value = value;
        this.decimals = decimals;
        this.precision = precision;
        this.factId = factId;
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
}
