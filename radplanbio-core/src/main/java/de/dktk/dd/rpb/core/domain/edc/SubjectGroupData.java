package de.dktk.dd.rpb.core.domain.edc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="SubjectGroupData", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
public class SubjectGroupData implements Serializable {

    @XmlAttribute(name="StudyGroupClassID", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1" )
    private String studyGroupClassID;

    @XmlAttribute(name="StudyGroupClassName", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1" )
    private String studyGroupClassName;

    @XmlAttribute(name="StudyGroupName", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1" )
    private String studyGroupName;

    public SubjectGroupData() {
    }

    public String getStudyGroupClassID() {
        return studyGroupClassID;
    }

    public void setStudyGroupClassID(String studyGroupClassID) {
        this.studyGroupClassID = studyGroupClassID;
    }

    public String getStudyGroupClassName() {
        return studyGroupClassName;
    }

    public void setStudyGroupClassName(String studyGroupClassName) {
        this.studyGroupClassName = studyGroupClassName;
    }

    public String getStudyGroupName() {
        return studyGroupName;
    }

    public void setStudyGroupName(String studyGroupName) {
        this.studyGroupName = studyGroupName;
    }
}
