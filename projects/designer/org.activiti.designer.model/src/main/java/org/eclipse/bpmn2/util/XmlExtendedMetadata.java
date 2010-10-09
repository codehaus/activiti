package org.eclipse.bpmn2.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;

public class XmlExtendedMetadata extends BasicExtendedMetaData {

    /**
     * 
     */
    public XmlExtendedMetadata() {
    }

    @Override
    public String getNamespace(EPackage ePackage) {
        /*
         * Unfortunately XMLSaveImpl.addNamespaceDeclarations does not consider the XML namespaces that are declared on features to
         * calculate correct prefixes. Instead it calls this method for all packages and assumes that namespace and prefix of the package is
         * correct. In our case the namespace of the package is different to the namespace of all features. We correct this my returning the
         * namespace of a "typical" feature.
         */
        EClass docRoot = getDocumentRoot(ePackage);
        if (docRoot != null) {
            for (EStructuralFeature feature : docRoot.getEStructuralFeatures()) {
                String namespace = getNamespace(feature);
                if (namespace != null && !namespace.equals(ePackage.getNsURI()))
                    return namespace;
            }
        }

        return super.getNamespace(ePackage);
    }
}

