package com.vedatech.admin.controller.sat;


import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import java.util.HashMap;
import java.util.Map;


public class DefaultNamespacePrefixMapper extends NamespacePrefixMapper {

        private Map<String, String> namespaceMap = new HashMap<>();

    public DefaultNamespacePrefixMapper() {
            namespaceMap.put("http://www.sat.gob.mx/cfd/3", "cfdi");
            namespaceMap.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        System.out.println("PASAMOS LA PRUEBA");
        }

        @Override
        public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {

            System.out.println("NAME SPACEURI" + namespaceUri + "SUGGESTION " + suggestion);
            return namespaceMap.getOrDefault(namespaceUri, suggestion);
        }


}
