/**
 * core package of the ddRPB platform
 */

@XmlSchema(
        namespace="http://www.cdisc.org/ns/odm/v1.3",
        xmlns = {
                @XmlNs(namespaceURI = "http://www.openclinica.org/ns/odm_ext_v130/v3.1", prefix = "OpenClinica"),
                @XmlNs(namespaceURI = "http://www.cdisc.org/ns/odm/v1.3", prefix = "")
                //@XmlNs(namespaceURI = "http://www.uniklinikum-dresden.de/rpb", prefix = "RadPlanBio")
        },
        elementFormDefault= XmlNsForm.QUALIFIED)
 
package de.dktk.dd.rpb.core;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;