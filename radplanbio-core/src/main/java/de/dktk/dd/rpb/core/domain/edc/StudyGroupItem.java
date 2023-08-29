package de.dktk.dd.rpb.core.domain.edc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="StudyGroupItem", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
public class StudyGroupItem implements Serializable {

    @XmlAttribute(name="Name")
    private String name;

    @XmlAttribute(name="Description")
    private String description;

    public StudyGroupItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
