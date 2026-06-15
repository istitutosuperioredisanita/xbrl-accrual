package it.iss.accrual.xbrl;


import it.iss.accrual.xbrl.dto.AccrualXbl;
import it.iss.accrual.xbrl.dto.ContextXbrl;
import it.iss.accrual.xbrl.dto.FactXbrl;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class) // Abilita il supporto Spring in JUnit 5
@ContextConfiguration(classes = ConfigurazioneApp.class) // Specifica la configurazione dei Bean
class AccrulaGeneratedXbrlTest {
    private final Logger log = LoggerFactory.getLogger(AccrulaGeneratedXbrlTest.class);
    @Autowired
    AccrualService accrualService;

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

        AccrualXbl accrual = new AccrualXbl();
        accrual.setEnte("ISS");
        accrual.setDocumentId("DOC_SKA_REND");

        accrual.getContexts().put("CTX_INT_2025", new ContextXbrl("CTX_INT_2025",
                LocalDate.of(2025, 01, 01),
                LocalDate.of(2025, 12, 31)));
         accrual.getContexts().put("CTX_IST_2025", new ContextXbrl("CTX_IST_2025",
                 LocalDate.of(2025, 01, 01),
                 null));


        accrual.getFacts().add(new FactXbrl("SP_AttivoTotale", new BigDecimal("1510000.00"),"2",null,null,accrual.getContexts().get("CTX_IST_2025")));
        accrual.getFacts().add(new FactXbrl("SP_PassivoTotale", new BigDecimal("1520000.00"),"2",null,null,accrual.getContexts().get("CTX_IST_2025")));
        accrual.getFacts().add(new FactXbrl("SP_ATT-A.1", new BigDecimal("1530000.00"),"2",null,"SPD_ATT-A.1",accrual.getContexts().get("CTX_IST_2025")));
        accrual.getFacts().add(new FactXbrl("SP_ATT-A.2", new BigDecimal("530000.00"),"2",null,"SP_ATT-A.2",accrual.getContexts().get("CTX_IST_2025")));
        accrual.getFacts().add(new FactXbrl("SP_PASS-B.1", new BigDecimal("1530000.00"),"2",null,"SP_PASS-B.1",accrual.getContexts().get("CTX_IST_2025")));
        accrual.getFacts().add(new FactXbrl("SP_PASS-B.2.1", new BigDecimal("530000.00"),"2",null,"SP_PASS-B.2.1",accrual.getContexts().get("CTX_IST_2025")));

        accrual.getFacts().add(new FactXbrl("CE_A.1", new BigDecimal("1530000.00"),"2",null,"SPD_ATT-A.1",accrual.getContexts().get("CTX_INT_2025")));
        accrual.getFacts().add(new FactXbrl("CE_A.2", new BigDecimal("530000.00"),"2",null,"SP_ATT-A.2",accrual.getContexts().get("CTX_INT_2025")));

       // AccrualService imp = new AccrualServiceImpl();

        // Verifica che il salvataggio avvenga senza errori
        Path outputPath = Path.of("stato_patrimoniale.xbrl");
        // Pulizia prima del  test
        byte[] fileContent = assertDoesNotThrow(() ->accrualService.generaFileXbrl(accrual));
        assertDoesNotThrow(() -> {
            Files.write(outputPath, fileContent);
        }, "La scrittura del file ha lanciato un'eccezione imprevista");
        // Opzionale: verifica che il file sia stato effettivamente creato
        assertTrue(Files.exists(outputPath));

    }
}
