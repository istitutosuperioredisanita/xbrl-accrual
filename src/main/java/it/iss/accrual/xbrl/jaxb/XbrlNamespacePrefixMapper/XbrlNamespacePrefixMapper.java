package it.iss.accrual.xbrl.jaxb.XbrlNamespacePrefixMapper;

import org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper;

public class XbrlNamespacePrefixMapper extends NamespacePrefixMapper {

    @Override
    public String getPreferredPrefix(String namespaceUri,
                                     String suggestion,
                                     boolean requirePrefix) {

        return switch (namespaceUri) {

            case "http://www.xbrl.org/2003/instance" -> "xbrli";
            case "http://www.xbrl.org/2003/linkbase" -> "link";
            case "http://www.w3.org/1999/xlink" -> "xlink";
            case "http://www.xbrl.org/2003/iso4217" -> "iso4217";

            // 🔥 MEF taxonomy
            case "http://www.rgs.mef.gov.it/xbrl/accrual/ska/roles/2025-04-14" -> "sr";

            default -> suggestion;
        };
    }
}