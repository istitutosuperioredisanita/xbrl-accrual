package it.iss.xbrl.accrual.generated;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JaxbContextTest {

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
}
