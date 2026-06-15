package it.iss.accrual.xbrl.jaxb.AccrualXbrlBuilder;

import it.iss.accrual.xbrl.jaxb.XbrlNamespacePrefixMapper.XbrlNamespacePrefixMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import org.xbrl._2003.instance.Context;
import org.xbrl._2003.instance.ContextEntityType;
import org.xbrl._2003.instance.ContextPeriodType;
import org.xbrl._2003.instance.MonetaryItemType;
import org.xbrl._2003.instance.Unit;
import org.xbrl._2003.instance.Xbrl;
import org.xbrl._2003.xlink.SimpleType;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MefAccrualDynamicBuilder {

    private static final String DEFAULT_ENTITY_SCHEME = "http://www.mef.gov.it";
    private static final String ISO4217_NS = "http://www.xbrl.org/2003/iso4217";

    private final org.xbrl._2003.instance.ObjectFactory xbrliFactory =
            new org.xbrl._2003.instance.ObjectFactory();

    private final org.xbrl._2003.xlink.ObjectFactory xlinkFactory =
            new org.xbrl._2003.xlink.ObjectFactory();

    private final it.gov.mef.rgs.xbrl.accrual.ska.roles._2025_04_14.ObjectFactory mefFactory =
            new it.gov.mef.rgs.xbrl.accrual.ska.roles._2025_04_14.ObjectFactory();

    private final Xbrl xbrl;

    public MefAccrualDynamicBuilder() {
        this.xbrl = xbrliFactory.createXbrl();
    }

    public Xbrl build() {
        return xbrl;
    }

    public it.gov.mef.rgs.xbrl.accrual.ska.roles._2025_04_14.ObjectFactory mefFactory() {
        return mefFactory;
    }

    public MefAccrualDynamicBuilder withDocumentId(String id) {
        xbrl.setId(id);
        return this;
    }

    public MefAccrualDynamicBuilder withSchemaRef(String href) {
        SimpleType schemaRef = xlinkFactory.createSimpleType();
        schemaRef.setType("simple");
        schemaRef.setHref(href);
        xbrl.getSchemaReves().add(schemaRef);
        return this;
    }

    // ----------------------------------------------------
    // CONTEXT TIPIZZATO
    // ----------------------------------------------------

    public Context createInstantContext(String contextId,
                                        String entityIdentifier,
                                        LocalDate instantDate) {
        return createInstantContext(contextId, DEFAULT_ENTITY_SCHEME, entityIdentifier, instantDate);
    }

    public Context createInstantContext(String contextId,
                                        String entityScheme,
                                        String entityIdentifier,
                                        LocalDate instantDate) {
        Context context = new Context();
        context.setId(contextId);
        context.setEntity(createEntity(entityScheme, entityIdentifier));

        ContextPeriodType period = new ContextPeriodType();
        period.setInstant(instantDate.toString());
        context.setPeriod(period);

        return context;
    }

    public Context createDurationContext(String contextId,
                                         String entityIdentifier,
                                         LocalDate startDate,
                                         LocalDate endDate) {
        return createDurationContext(contextId, DEFAULT_ENTITY_SCHEME, entityIdentifier, startDate, endDate);
    }

    public Context createDurationContext(String contextId,
                                         String entityScheme,
                                         String entityIdentifier,
                                         LocalDate startDate,
                                         LocalDate endDate) {
        Context context = new Context();
        context.setId(contextId);
        context.setEntity(createEntity(entityScheme, entityIdentifier));

        ContextPeriodType period = new ContextPeriodType();
        period.setStartDate(startDate.toString());
        period.setEndDate(endDate.toString());
        context.setPeriod(period);

        return context;
    }

    private ContextEntityType createEntity(String entityScheme, String entityIdentifier) {
        ContextEntityType entity = new ContextEntityType();
        ContextEntityType.Identifier identifier = new ContextEntityType.Identifier();
        identifier.setScheme(entityScheme);
        identifier.setValue(entityIdentifier);
        entity.setIdentifier(identifier);
        return entity;
    }

    public MefAccrualDynamicBuilder addContext(Context context) {
        xbrl.getItemsAndTuplesAndContexts().add(context);
        return this;
    }

    // ----------------------------------------------------
    // UNIT TIPIZZATA
    // ----------------------------------------------------

    public Unit createUnit(String unitId, QName measure) {
        Unit unit = new Unit();
        unit.setId(unitId);
        unit.getMeasures().add(measure);
        return unit;
    }

    public Unit createUnitEUR(String unitId) {
        return createUnit(unitId, new QName(ISO4217_NS, "EUR", "iso4217"));
    }

    public MefAccrualDynamicBuilder addUnit(Unit unit) {
        xbrl.getItemsAndTuplesAndContexts().add(unit);
        return this;
    }

    // ----------------------------------------------------
    // FACT MONETARIO
    // ----------------------------------------------------

    public MonetaryItemType monetary(BigDecimal value,
                                     Context contextRef,
                                     Unit unitRef,
                                     String decimals,
                                     String precision,
                                     String factId) {
        MonetaryItemType item = new MonetaryItemType();
        item.setValue(value);
        item.setContextRef(contextRef);
        item.setUnitRef(unitRef);

        if (decimals != null && !decimals.isBlank()) {
            item.setDecimals(decimals);
        }
        if (precision != null && !precision.isBlank()) {
            item.setPrecision(precision);
        }
        if (factId != null && !factId.isBlank()) {
            item.setId(factId);
        }
        return item;
    }

    public MefAccrualDynamicBuilder addFact(JAXBElement<MonetaryItemType> fact) {
        xbrl.getItemsAndTuplesAndContexts().add(fact);
        return this;
    }

    /**
     * Dynamic fact builder:
     * esempi input:
     * - "SP_AttivoTotale"
     * - "SP_PassivoTotale"
     * - "CE_A.1"
     * - "SPD_ATT-A.1_conc"
     * - "CE_Conto-Economico"
     */
    public MefAccrualDynamicBuilder addMonetaryFact(String taxonomyElementName,
                                                    BigDecimal value,
                                                    Context contextRef,
                                                    Unit unitRef,
                                                    String decimals,
                                                    String precision,
                                                    String factId) {
        JAXBElement<MonetaryItemType> fact = createDynamicMonetaryFact(
                taxonomyElementName, value, contextRef, unitRef, decimals, precision, factId
        );
        return addFact(fact);
    }

    public JAXBElement<MonetaryItemType> createDynamicMonetaryFact(String taxonomyElementName,
                                                                   BigDecimal value,
                                                                   Context contextRef,
                                                                   Unit unitRef,
                                                                   String decimals,
                                                                   String precision,
                                                                   String factId) {
        try {
            String methodName = "create" + normalizeTaxonomyElementName(taxonomyElementName);
            Method method = it.gov.mef.rgs.xbrl.accrual.ska.roles._2025_04_14.ObjectFactory.class
                    .getMethod(methodName, MonetaryItemType.class);

            MonetaryItemType item = monetary(value, contextRef, unitRef, decimals, precision, factId);

            @SuppressWarnings("unchecked")
            JAXBElement<MonetaryItemType> fact =
                    (JAXBElement<MonetaryItemType>) method.invoke(mefFactory, item);

            return fact;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Nessun metodo factory trovato per elemento tassonomico: "
                            + taxonomyElementName
                            + " -> metodo atteso: create" + normalizeTaxonomyElementName(taxonomyElementName),
                    e
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Errore nella creazione dinamica del fact per: " + taxonomyElementName,
                    e
            );
        }
    }

    /**
     * Normalizza il local name del fact verso il nome del metodo JAXB generato.
     *
     * Esempi:
     * - SP_AttivoTotale   -> SPAttivoTotale
     * - CE_A.1            -> CEA1
     * - SPD_ATT-A.1_conc  -> SPDATTA1Conc
     * - CE_Conto-Economico -> CEContoEconomico
     */
    public static String normalizeTaxonomyElementName(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Nome tassonomico vuoto");
        }

        Matcher matcher = Pattern.compile("[A-Za-z0-9]+").matcher(raw.trim());
        StringBuilder out = new StringBuilder();
        int tokenIndex = 0;

        while (matcher.find()) {
            String token = matcher.group();
            if (tokenIndex == 0 && token.equals(token.toUpperCase(Locale.ROOT))) {
                out.append(token);
            } else {
                out.append(Character.toUpperCase(token.charAt(0)));
                if (token.length() > 1) {
                    out.append(token.substring(1));
                }
            }
            tokenIndex++;
        }

        if (out.isEmpty()) {
            throw new IllegalArgumentException("Impossibile normalizzare il nome tassonomico: " + raw);
        }

        return out.toString();
    }

    public byte[] marshal() throws JAXBException {


        JAXBContext ctx = JAXBContext.newInstance(
                org.xbrl._2003.instance.Xbrl.class,
                it.gov.mef.rgs.xbrl.accrual.ska.roles._2025_04_14.ObjectFactory.class,
                org.xbrl._2003.instance.ObjectFactory.class,
                org.xbrl._2003.xlink.ObjectFactory.class,
                org.xbrl._2003.linkbase.ObjectFactory.class
        );
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(
                "org.glassfish.jaxb.namespacePrefixMapper",
                new XbrlNamespacePrefixMapper()
        );
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        marshaller.marshal(xbrl, os);
        return os.toByteArray();
    }

}
