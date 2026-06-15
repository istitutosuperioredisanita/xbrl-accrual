package it.iss.accrual.xbrl.dto;

import java.util.ArrayList;
import java.util.List;


public class Accrual {

    String ente;

    Integer annoBilancio;

    List<Fact> facts= new ArrayList<>();

    public String getEnte() {
        return ente;
    }

    public void setEnte(String ente) {
        this.ente = ente;
    }

    public Integer getAnnoBilancio() {
        return annoBilancio;
    }

    public void setAnnoBilancio(Integer annoBilancio) {
        this.annoBilancio = annoBilancio;
    }

    public List<Fact> getFacts() {
        return facts;
    }

    public void setFacts(List<Fact> facts) {
        this.facts = facts;
    }
}
