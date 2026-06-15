package it.iss.accrual.xbrl;

import it.iss.accrual.xbrl.dto.AccrualXbl;
import it.iss.accrual.xbrl.dto.ContextXbrl;
import it.iss.accrual.xbrl.jaxb.AccrualXbrlBuilder.MefAccrualDynamicBuilder;
import jakarta.xml.bind.JAXBException;
import org.xbrl._2003.instance.Context;
import org.xbrl._2003.instance.Unit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccrualServiceImpl implements AccrualService{

    private String getDocumentId(AccrualXbl dati){
        return "DOC_".concat(dati.getAnnoBilancio().toString());
    }
    private Context getContext(MefAccrualDynamicBuilder builder,ContextXbrl context, String ente){
        if ( !Optional.ofNullable(context).isPresent())
            return null;
        if (Optional.ofNullable(context.getEndDate()).isPresent()){
            return builder.createDurationContext(
                    context.getId(),
                    ente,
                   context.getStartDate(),
                    context.getEndDate());
        }
        return builder.createInstantContext(context.getId(),
                ente,
                context.getStartDate());
    }

    private Map<String, Context> getContexts (MefAccrualDynamicBuilder builder , AccrualXbl dati){
        //String context= "CTX_".concat(dati.getAnnoBilancio().toString());
        Map<String, Context> contexts = new HashMap<String, Context>();
        dati.getContexts().values().forEach(context->{
             contexts.put(context.getId(),getContext( builder,context, dati.getEnte()));
        });
        return contexts;
    }
    @Override
    public byte[] generaFileXbrl(AccrualXbl dati) throws JAXBException {
        if (!Optional.ofNullable(dati).isPresent())
            throw new RuntimeException("Dati non presenti");

        if (!Optional.ofNullable(dati.getFacts()).isPresent()||dati.getFacts().isEmpty())
            throw new RuntimeException("Facts non presenti");

        MefAccrualDynamicBuilder builder = new MefAccrualDynamicBuilder()
                .withDocumentId(getDocumentId(dati))
                .withSchemaRef("accrual-ska-rend-lab-it_2025-04-14.xsd");

        Map<String, Context> ctx = getContexts( builder,dati);
        Unit eur = builder.createUnitEUR("EUR");
        builder.addUnit(eur);
        ctx.values().forEach(c->{
            builder.addContext(c);
        });

        dati.getFacts().forEach(fact ->{
             builder.addMonetaryFact(fact.getTaxonomy(),
                    fact.getValue(), ctx.get( fact.getContext().getId()), eur, fact.getDecimals(), fact.getPrecision(), fact.getFactId());
        } );

        return builder.marshal();

    }


}
