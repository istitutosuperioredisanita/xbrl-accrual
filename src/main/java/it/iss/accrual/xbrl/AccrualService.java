package it.iss.accrual.xbrl;

import it.iss.accrual.xbrl.dto.AccrualXbl;
import jakarta.xml.bind.JAXBException;
import org.springframework.stereotype.Service;


public interface AccrualService {

    byte[] generaFileXbrl(AccrualXbl dati) throws JAXBException;


}
