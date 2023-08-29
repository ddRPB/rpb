package de.dktk.dd.rpb.core.domain.edc;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="StudyGroupClassList", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
public class StudyGroupClassList implements Serializable {

    @XmlAttribute(name="ID")
    private String id;

    @XmlAttribute(name="Name")
    private String name;

    @XmlAttribute(name="DataType")
    private String dataType;

    @XmlAttribute(name="ActualDataType")
    private String actualDataType;

    @XmlElement(name="StudyGroupItem", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private List<StudyGroupItem> studyGroupItemList;

    public StudyGroupClassList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getActualDataType() {
        return actualDataType;
    }

    public void setActualDataType(String actualDataType) {
        this.actualDataType = actualDataType;
    }

    public List<StudyGroupItem> getStudyGroupItemList() {
        return studyGroupItemList;
    }

    public void setStudyGroupItemList(List<StudyGroupItem> studyGroupItemList) {
        this.studyGroupItemList = studyGroupItemList;
    }
}
