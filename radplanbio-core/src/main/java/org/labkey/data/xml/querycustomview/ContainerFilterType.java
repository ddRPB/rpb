//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml.querycustomview;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für containerFilterType.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="containerFilterType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Current"/>
 *     &lt;enumeration value="CurrentWithUser"/>
 *     &lt;enumeration value="CurrentAndFirstChildren"/>
 *     &lt;enumeration value="CurrentAndSubfolders"/>
 *     &lt;enumeration value="CurrentAndSiblings"/>
 *     &lt;enumeration value="CurrentOrParentAndWorkbooks"/>
 *     &lt;enumeration value="CurrentPlusProject"/>
 *     &lt;enumeration value="CurrentAndParents"/>
 *     &lt;enumeration value="CurrentPlusProjectAndShared"/>
 *     &lt;enumeration value="AssayLocation"/>
 *     &lt;enumeration value="WorkbookAndParent"/>
 *     &lt;enumeration value="StudyAndSourceStudy"/>
 *     &lt;enumeration value="AllFolders"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "containerFilterType")
@XmlEnum
public enum ContainerFilterType {


    /**
     * The current container.
     */
    @XmlEnumValue("Current")
    CURRENT("Current"),

    /**
     * Current folder with permissions applied to user.
     */
    @XmlEnumValue("CurrentWithUser")
    CURRENT_WITH_USER("CurrentWithUser"),

    /**
     * Current folder and first children that are not workbooks.
     */
    @XmlEnumValue("CurrentAndFirstChildren")
    CURRENT_AND_FIRST_CHILDREN("CurrentAndFirstChildren"),

    /**
     * The current container and any folders it contains.
     */
    @XmlEnumValue("CurrentAndSubfolders")
    CURRENT_AND_SUBFOLDERS("CurrentAndSubfolders"),

    /**
     * Current folder and siblings.
     */
    @XmlEnumValue("CurrentAndSiblings")
    CURRENT_AND_SIBLINGS("CurrentAndSiblings"),

    /**
     * Current folder and/or parent if the current folder is a workbook, plus all workbooks in this series.
     */
    @XmlEnumValue("CurrentOrParentAndWorkbooks")
    CURRENT_OR_PARENT_AND_WORKBOOKS("CurrentOrParentAndWorkbooks"),

    /**
     * The current container and the project folder container it.
     */
    @XmlEnumValue("CurrentPlusProject")
    CURRENT_PLUS_PROJECT("CurrentPlusProject"),

    /**
     * The current container and all of its parent containers.
     */
    @XmlEnumValue("CurrentAndParents")
    CURRENT_AND_PARENTS("CurrentAndParents"),

    /**
     * The current container, its project folder and all shared folders.
     */
    @XmlEnumValue("CurrentPlusProjectAndShared")
    CURRENT_PLUS_PROJECT_AND_SHARED("CurrentPlusProjectAndShared"),

    /**
     * Current folder, project, and Shared project.
     */
    @XmlEnumValue("AssayLocation")
    ASSAY_LOCATION("AssayLocation"),

    /**
     * Current workbook and parent.
     */
    @XmlEnumValue("WorkbookAndParent")
    WORKBOOK_AND_PARENT("WorkbookAndParent"),

    /**
     * Current study and its source/parent study.
     */
    @XmlEnumValue("StudyAndSourceStudy")
    STUDY_AND_SOURCE_STUDY("StudyAndSourceStudy"),

    /**
     * All folders to which the user has permission.
     */
    @XmlEnumValue("AllFolders")
    ALL_FOLDERS("AllFolders");
    private final String value;

    ContainerFilterType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ContainerFilterType fromValue(String v) {
        for (ContainerFilterType c : ContainerFilterType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
