package it.iss.accrual.xbrl;

import java.io.Serializable;

public class NoDataNotFoundException extends Exception implements Serializable {
    public  NoDataNotFoundException(String message) {
        super(message);
    }
}