package it.iss.accrual.xbrl;

import it.iss.accrual.xbrl.dto.Accrual;
import it.iss.accrual.xbrl.jaxb.AccrualXbrlBuilder.MefAccrualDynamicBuilder;
import jakarta.xml.bind.JAXBException;
import org.xbrl._2003.instance.Context;
import org.xbrl._2003.instance.Unit;

import java.time.LocalDate;
import java.util.Optional;

public class AccrualServiceImpl implements AccrualService{

    private String getDocumentId(Accrual dati){
        return "DOC_".concat(dati.getAnnoBilancio().toString());
    }
    private Context getContext (MefAccrualDynamicBuilder builder , Accrual dati){
        String context= "CTX_".concat(dati.getAnnoBilancio().toString());
        return  builder.createDurationContext(
                context,
                dati.getEnte(),
                LocalDate.of(2025, 01, 01),
                LocalDate.of(2025, 12, 31));
    }
    @Override
    public byte[] generaFileXbrl(Accrual dati) throws JAXBException {
        if (!Optional.ofNullable(dati).isPresent())
            throw new RuntimeException("Dati non presenti");

        if (!Optional.ofNullable(dati.getFacts()).isPresent()||dati.getFacts().isEmpty())
            throw new RuntimeException("Facts non presenti");

        MefAccrualDynamicBuilder builder = new MefAccrualDynamicBuilder()
                .withDocumentId(getDocumentId(dati))
                .withSchemaRef("accrual-ska-rend-lab-it_2025-04-14.xsd");

        Context ctx = getContext( builder,dati);
        Unit eur = builder.createUnitEUR("EUR");
        builder.addUnit(eur);
        builder.addContext(ctx);
        dati.getFacts().forEach(fact ->{
             builder.addMonetaryFact(fact.getTaxonomy(),
                    fact.getValue(), ctx, eur, fact.getDecimals(), fact.getPrecision(), fact.getFactId());
        } );

        return builder.marshal();



    }


}
