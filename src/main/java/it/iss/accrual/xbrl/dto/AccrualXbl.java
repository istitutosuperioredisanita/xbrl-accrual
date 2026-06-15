package it.iss.accrual.xbrl.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AccrualXbl {

    String ente;

    Integer annoBilancio;

    Map<String,ContextXbrl> contexts = new HashMap<String,ContextXbrl>();

    List<FactXbrl> facts= new ArrayList<>();

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

    public List<FactXbrl> getFacts() {
        return facts;
    }

    public void setFacts(List<FactXbrl> facts) {
        this.facts = facts;
    }

    public Map<String, ContextXbrl> getContexts() {
        return contexts;
    }

    public void setContexts(Map<String, ContextXbrl> contexts) {
        this.contexts = contexts;
    }
}
