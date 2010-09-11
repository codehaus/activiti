package org.eclipse.bpmn2.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.xmi.XMLSave.XMLTypeInfo;

public class OnlyContainmentTypeInfo implements XMLTypeInfo {

    /*
     * Ensure that we save type information only for containment - in this case using the substitution group magic. But don't save type
     * information for normal references. We anyway never create proxies.
     */

    // @Override // for implementing interface methods: only since Java 1.6
    public boolean shouldSaveType(EClass objectType, EClassifier featureType,
            EStructuralFeature feature) {
        return feature instanceof EReference && ((EReference) feature).isContainment();
    }

    // @Override
    public boolean shouldSaveType(EClass objectType, EClass featureType, EStructuralFeature feature) {
        return feature instanceof EReference && ((EReference) feature).isContainment();
    }

}

