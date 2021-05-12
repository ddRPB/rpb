package de.dktk.dd.rpb.core.domain.edc;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * EventDefinitionDetails domain entity (based on CDISC ODM StudyEventDef.EventDefinitionDetails)
 */
public class EventDefinitionDetails implements Serializable {


    private String oid;
    private String description;
    private String category;


    public EventDefinitionDetails() {
    }

    public String getOid() {
        return oid;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    @XmlAttribute(name = "StudyEventOID")
    public void setOid(String oid) {
        this.oid = oid;
    }

    @XmlElement(name = "Description", namespace = "http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "Category", namespace = "http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    public void setCategory(String category) {
        this.category = category;
    }
}
