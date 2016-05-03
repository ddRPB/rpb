/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2015 Tomas Skripcak
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dktk.dd.rpb.core.domain.edc;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import org.apache.log4j.Logger;

import javax.persistence.Transient;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * StudyEventDefinition transient domain entity (CDISC ODM)
 *
 * @author tomas@skripcak.net
 * @since 21 Oct 2013
 * @version 1.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="StudyEventDef")
public class EventDefinition implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(EventDefinition.class);

    //endregion

    //region Members

    private Integer id; // unused for Transient entity

    private DatatypeFactory dataTypeFactory;

    @XmlAttribute(name="OID")
    private String oid;

    @XmlAttribute(name="Name")
    private String name;

    private String description;
    private String category;

    @XmlAttribute(name="Type")
    private String type;

    private boolean isRepeating;

    private String status;
    private int subjectAgeAtEvent;
    private int studyEventRepeatKey;

    @XmlElement(name="FormRef")
    private List<FormDefinition> formRefs;

    private List<FormDefinition> formDefs;

    private XMLGregorianCalendar startDate;

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public EventDefinition() {

        try {
            dataTypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        this.formDefs = new ArrayList<FormDefinition>();
        this.formRefs = new ArrayList<FormDefinition>();
    }

    //endregion

    //region Properties

    //region Id

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    @Transient
    public boolean isIdSet() {
        return this.id != null;
    }

    //endregion

    //region OID

    public String getOid() {
        return this.oid;
    }

    public void setOid(String value) {
        this.oid = value;
    }

    //endregion

    //region Name

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

    //region FormRefs

    public List<FormDefinition> getFormRefs() {
        return this.formRefs;
    }

    public void setFormRefs(List<FormDefinition> list) {
        this.formRefs = list;
    }

    //endregion

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getCategory() { return this.category; }

    public void setCategory(String value) {
        this.category = value;
    }

    //region Type

    public String getType() {
        return this.type;
    }

    public void setType(String value) {
        this.type = value;
    }

    //endregion

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public int getSubjectAgeAtEvent() {
        return this.subjectAgeAtEvent;
    }

    public void setSubjectAgeAtEvent(int value) {
        if (this.subjectAgeAtEvent != value) {
            this.subjectAgeAtEvent = value;
        }
    }

    public int getStudyEventRepeatKey() {
        return this.studyEventRepeatKey;
    }

    public void setStudyEventRepeatKey(int value) {
        if (this.studyEventRepeatKey != value) {
            this.studyEventRepeatKey = value;
        }
    }

    public boolean getIsRepeating() {
        return this.isRepeating;
    }

    public void setIsRepeating(boolean value) {
        this.isRepeating = value;
    }

    public List<FormDefinition> getFormDefs() {
        return this.formDefs;
    }

    public void setFormDefs(List<FormDefinition> list) {
        this.formDefs = list;
    }

    /**
     * Get event start date
     * @return event start date
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Set event start date
     * @param startDate event start date
     */
    public void setStartDate(XMLGregorianCalendar startDate) {
        this.startDate = startDate;
        if (this.startDate != null) {
            this.startDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        }
    }

    /**
     * Set event start date from String
     * @param startDate event start date as String (yyyy-mm-dd)
     */
    public void setStartDate(String startDate) {
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        Calendar cal = null;
        try {
            Date datum  = df.parse(startDate);

            // Convert Date to a Calendar
            cal = GregorianCalendar.getInstance();
            cal.setTime(datum);

            // mutate the value
            //cal.add(Calendar.YEAR, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.setStartDate(this.dataTypeFactory.newXMLGregorianCalendar((GregorianCalendar)cal));
    }

    //endregion

    //region Methods

    /**
     * Find eCRF item definition in Event according to provided OID
     *
     * @param itemDefOid item definition OID
     * @return item definition object instance
     */
    public ItemDefinition findItemDef(String itemDefOid) {
        for (FormDefinition formDef : this.formDefs) {
            for (ItemGroupDefinition itemGroupDef: formDef.getItemGroupDefs()) {
                for (ItemDefinition itemDef : itemGroupDef.getItemDefs()) {
                    if (itemDef.getOid().equals(itemDefOid)) {
                        return itemDef;
                    }
                }
            }
        }

        return null;
    }

    //endregion

    //region Overrides

    /**
     * Compare this entity to other provided entity
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof EventDefinition && this.hashCode() == other.hashCode());
    }

    /**
     * Generate hash code for this entity
     * @return hash code of the entity
     */
    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this, this.oid);
    }

    /**
     * String representation of entity
     * @return string representation of the entity
     */
    @Override
    public String toString() {
        return this.oid;
    }

    //endregion

}
