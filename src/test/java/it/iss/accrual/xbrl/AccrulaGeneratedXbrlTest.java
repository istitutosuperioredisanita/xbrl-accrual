package it.iss.accrual.xbrl;


import it.iss.accrual.xbrl.dto.Accrual;
import it.iss.accrual.xbrl.dto.Fact;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;


import static org.junit.jupiter.api.Assertions.*;


class AccrulaGeneratedXbrlTest {
    private final Logger log = LoggerFactory.getLogger(AccrulaGeneratedXbrlTest.class);

    @Test
    void allPackagesContextInitializes() throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(
            org.xbrl._2003.instance.ObjectFactory.class,
            org.xbrl._2003.linkbase.ObjectFactory.class,
            org.xbrl._2003.xlink.ObjectFactory.class,
            org.xbrl.dtr.type.numeric.ObjectFactory.class,
            org.xbrl.dtr.type.non_numeric.ObjectFactory.class,
            it.gov.mef.rgs.xbrl.accrual.ska.roles._2025_04_14.ObjectFactory.class
        );
        assertNotNull(ctx);
    }

    @Test
    void generateStatoPatrimonialeXbrl() {

        Accrual accrual = new Accrual();
        accrual.setEnte("ISS");
        accrual.setAnnoBilancio(2025);
        accrual.getFacts().add(new Fact("SP_AttivoTotale", new BigDecimal("1510000.00"),"2",null,null));
        accrual.getFacts().add(new Fact("SP_PassivoTotale", new BigDecimal("1520000.00"),"2",null,null));
        accrual.getFacts().add(new Fact("SP_ATT-A.1", new BigDecimal("1530000.00"),"2",null,"SPD_ATT-A.1"));
        accrual.getFacts().add(new Fact("SP_ATT-A.2", new BigDecimal("530000.00"),"2",null,"SP_ATT-A.2"));
        accrual.getFacts().add(new Fact("SP_PASS-B.1", new BigDecimal("1530000.00"),"2",null,"SP_PASS-B.1"));
        accrual.getFacts().add(new Fact("SP_PASS-B.2.1", new BigDecimal("530000.00"),"2",null,"SP_PASS-B.2.1"));

        AccrualService imp = new AccrualServiceImpl();

        // Verifica che il salvataggio avvenga senza errori
        Path outputPath = Path.of("stato_patrimoniale.xbrl");
        // Pulizia prima del  test
        byte[] fileContent = assertDoesNotThrow(() ->imp.generaFileXbrl(accrual));
        assertDoesNotThrow(() -> {
            Files.write(outputPath, fileContent);
        }, "La scrittura del file ha lanciato un'eccezione imprevista");
        // Opzionale: verifica che il file sia stato effettivamente creato
        assertTrue(Files.exists(outputPath));

    }
}
