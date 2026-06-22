package it.iss.accrual.xbrl;

import java.io.Serializable;

public class AccrualXbrException extends Exception implements Serializable {
    public AccrualXbrException() {
    }

    public AccrualXbrException(String s) {
        super(s);
    }

    public AccrualXbrException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AccrualXbrException(Throwable throwable) {
        super(throwable);
    }
}
