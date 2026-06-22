package it.iss.accrual.xbrl;

import it.iss.accrual.xbrl.dto.AccrualXbrl;
import jakarta.xml.bind.JAXBException;


public interface AccrualService {

    byte[] generaFileXbrl(AccrualXbrl dati) throws AccrualXbrException,NoDataNotFoundException;


}
