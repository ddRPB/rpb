package de.dktk.dd.rpb.core.builder.edc;

import de.dktk.dd.rpb.core.domain.edc.ClinicalData;
import de.dktk.dd.rpb.core.domain.edc.Odm;

public interface IOdmBuilder {
    Odm build();

    OdmBuilder addClinicalData(ClinicalData clinicalData);
}
