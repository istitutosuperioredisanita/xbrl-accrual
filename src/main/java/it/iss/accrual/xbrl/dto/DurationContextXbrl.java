package it.iss.accrual.xbrl.dto;

import java.time.LocalDate;

public class DurationContextXbrl extends ContextXbrl{
    LocalDate startDate;

    LocalDate endDate;

    public DurationContextXbrl(String id, String entityIdentifier,LocalDate startDate, LocalDate endDate) {
        super(id,entityIdentifier);
        this.startDate = startDate;
        this.endDate = endDate;
    }



    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }



}
