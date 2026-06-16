package it.iss.accrual.xbrl.dto;

import java.time.LocalDate;
import java.util.Objects;

public abstract class ContextXbrl {
    String id;

    String entityIdentifier;

    public ContextXbrl(String id,String entityIdentifier) {
        this.id = id;
        this.entityIdentifier=entityIdentifier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    public void setEntityIdentifier(String entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }
}
