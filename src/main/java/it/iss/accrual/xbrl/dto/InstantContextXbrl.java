package it.iss.accrual.xbrl.dto;

import java.time.LocalDate;

public class InstantContextXbrl extends ContextXbrl{

    LocalDate instant;

    public InstantContextXbrl(String id, String entityIdentifier,LocalDate instant) {
        super(id,entityIdentifier);
        this.instant = instant;

    }

    public LocalDate getInstant() {
        return instant;
    }

    public void setInstant(LocalDate instant) {
        this.instant = instant;
    }
}
