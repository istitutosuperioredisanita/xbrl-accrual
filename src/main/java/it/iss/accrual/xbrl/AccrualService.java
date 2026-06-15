package it.iss.accrual.xbrl;

import it.iss.accrual.xbrl.dto.Accrual;
import jakarta.xml.bind.JAXBException;

public interface AccrualService {

    byte[] generaFileXbrl(Accrual dati) throws JAXBException;


}
