/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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

import com.google.common.base.Objects;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.OC_DATEFORMAT;
import static de.dktk.dd.rpb.core.util.Constants.OC_TIMESTAMPFORMAT;

/**
 * EventData domain entity (based on CDISC ODM StudyEventData)
 *
 * @author tomas@skripcak.net
 * @since 24 Feb 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StudyEventData")
public class EventData implements Identifiable<Integer>, Serializable, Comparable<EventData> {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(EventData.class);

    //endregion

    //region Members

    @XmlTransient
    private Integer id;

    @XmlAttribute(name = "StudyEventOID")
    private String studyEventOid;

    @XmlAttribute(name = "EventName", namespace = "http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String eventName;

    @XmlAttribute(name = "StartDate", namespace = "http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String startDate;

    @XmlTransient
    private String endDate;

    @XmlAttribute(name = "Status", namespace = "http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String status;

    @XmlTransient
    private String systemStatus;

    @XmlAttribute(name = "StudyEventRepeatKey")
    private String studyEventRepeatKey;

    @XmlTransient
    private EventDefinition eventDefinition;

    @XmlElement(name = "FormData")
    private List<FormData> formDataList;

    @XmlTransient
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder(); // Object hash

    //endregion

    //region Constructors

    public EventData() {
        // NOOP
    }

    public EventData(String studyEventOid) {
        this();

        this.studyEventOid = studyEventOid;
    }

    public EventData(String studyEventOid, String studyEventRepeatKey) {
        this(studyEventOid);

        this.studyEventRepeatKey = studyEventRepeatKey;
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

    //region StudyEventOID

    public String getStudyEventOid() {
        return this.studyEventOid;
    }

    public void setStudyEventOid(String value) {
        this.studyEventOid = value;
    }

    //endregion

    //region EventName

    public String getEventName() {
        if (this.eventName != null) {
            return this.eventName;
        } else if (this.eventDefinition != null) {
            return this.eventDefinition.getName();
        }

        return null;
    }

    public void setEventName(String value) {
        this.eventName = value;
    }

    //endregion

    //region StartDate

    public String getStartDate() {
        return this.startDate;
    }

    public Date getComparableStartDate() {
        if (this.startDate != null && !this.startDate.equals("")) {
            DateFormat format = new SimpleDateFormat(OC_DATEFORMAT);
            try {
                return format.parse(this.startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public LocalDateTime getStartDateAsLocalDate(){
        if(this.startDate.isEmpty()){
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(OC_TIMESTAMPFORMAT);
        return LocalDateTime.parse(this.startDate, formatter);

    }

    public String getComparableStartDateString() {
        DateFormat format = new SimpleDateFormat(Constants.RPB_DATEFORMAT);
        Date date = this.getComparableStartDate();
        return date != null ? format.format(date) : null;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(Date startDate) {
        DateFormat format = new SimpleDateFormat(OC_DATEFORMAT);
        this.startDate = format.format(startDate);
    }

    public void setStartDate(XMLGregorianCalendar startDate) {
        this.setStartDate(startDate.toGregorianCalendar().getTime());
    }

    //endregion

    //region EndDate

    public String getEndDate() {
        return this.endDate;
    }

    public Date getComparableEndDate() {
        if (this.endDate != null && !this.endDate.equals("")) {
            DateFormat format = new SimpleDateFormat(OC_DATEFORMAT);
            try {
                return format.parse(this.startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public LocalDateTime getEndDateAsLocalDate(){
        if(this.endDate.isEmpty()){
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(OC_TIMESTAMPFORMAT);
        return LocalDateTime.parse(this.endDate, formatter);

    }

    public String getComparableEndDateString() {
        DateFormat format = new SimpleDateFormat(Constants.RPB_DATEFORMAT);
        Date date = this.getComparableEndDate();
        return date != null ? format.format(date) : null;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(Date endDate) {
        DateFormat format = new SimpleDateFormat(OC_DATEFORMAT);
        this.endDate = format.format(endDate);
    }

    public void setEndDate(XMLGregorianCalendar endDate) {
        this.setEndDate(endDate.toGregorianCalendar().getTime());
    }

    //endregion

    //region Status

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    //endregion

    //region SystemStatus

    public String getSystemStatus() {
        return this.systemStatus;
    }

    public void setSystemStatus(String value) {
        this.systemStatus = value;
    }

    //endregion

    //region StudyEventRepeatKey

    public String getStudyEventRepeatKey() {
        return this.studyEventRepeatKey;
    }

    public Integer getStudyEventRepeatKeyInteger() {
        if (this.studyEventRepeatKey != null && !this.studyEventRepeatKey.isEmpty()) {
            return Integer.parseInt(this.studyEventRepeatKey);
        } else {
            return null;
        }
    }

    public void setStudyEventRepeatKey(String value) {
        this.studyEventRepeatKey = value;
    }

    //endregion

    //region EventDefinition

    public EventDefinition getEventDefinition() {
        return this.eventDefinition;
    }

    public void setEventDefinition(EventDefinition eventDefinition) {
        this.eventDefinition = eventDefinition;
    }

    //endregion

    //region FormData

    public List<FormData> getFormDataList() {
        return this.formDataList;
    }

    public void setFormDataList(List<FormData> list) {
        this.formDataList = list;
    }

    /**
     * Helper method to add the passed {@link FormData} to the formDataList list
     */
    public boolean addFormData(FormData form) {
        if (!this.containsFormData(form)) {
            if (this.formDataList == null) {
                this.formDataList = new ArrayList<>();
            }
            return this.formDataList.add(form);
        }

        return false;
    }

    /**
     * Helper method to remove the passed {@link FormData} from the formDataList list
     */
    public boolean removeFormData(FormData fromData) {
        return this.containsFormData(fromData) && this.formDataList.remove(fromData);
    }

    /**
     * Helper method to determine if the passed {@link FormData} is present in the formDataList list
     */
    public boolean containsFormData(FormData fromData) {
        return this.formDataList != null && this.formDataList.contains(fromData);
    }

    /**
     * Helper method to determine whether passed form OID is present in the formDataList list
     *
     * @param formOid formOid to lookup for
     * @return true if the formData with specified form OID exists within event
     */
    public boolean containsFormData(String formOid) {
        if (this.formDataList != null) {
            for (FormData formData : this.formDataList) {
                if (formData.getFormOid().equals(formOid)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Helper method to get formData according to form OID
     *
     * @param formOid formOid to lookup for
     * @return formData if exists
     */
    public FormData findFormData(String formOid) {
        if (this.formDataList != null) {
            for (FormData formData : this.formDataList) {
                if (formData.getFormOid().equals(formOid)) {
                    return formData;
                }
            }
        }

        return null;
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof EventData && hashCode() == other.hashCode());
    }

    /**
     * Generate entity hash code
     *
     * @return hash
     */
    @Override
    public int hashCode() {
        String alternateIdentifier = this.studyEventRepeatKey != null ? this.studyEventOid + "_" + this.studyEventRepeatKey : this.studyEventOid;
        return identifiableHashBuilder.hash(log, this, alternateIdentifier);
    }

    /**
     * Construct a readable string representation for this RtStructType instance.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("studyEventOid", this.studyEventOid)
                .add("studyEventRepeatKey", this.studyEventRepeatKey)
                .toString();
    }

    //endregion

    //region Methods

    //region Metadata

    public void linkOdmDefinitions(Odm odm) {
        if (odm != null) {

            // EventDefinition linking
            EventDefinition eventDefinition = odm.findUniqueEventDefinitionOrNone(this.studyEventOid);
            if (eventDefinition != null) {
                this.setEventDefinition(eventDefinition);
            }
            // EventDefinition does not exists in metadata - event removed
            else {
                this.setStatus(EnumEventDataStatus.INVALID.toString());
            }

            // Link next level in hierarchy (CRF)
            if (this.formDataList != null) {
                for (FormData formData : this.formDataList) {
                    formData.linkOdmDefinitions(odm);
                }
            }
        }
    }

    //endregion

    //region Data

    public List<ItemData> getAllItemData() {
        List<ItemData> results = new ArrayList<>();

        if (this.formDataList != null) {
            for (FormData formData : this.formDataList) {
                results.addAll(formData.getAllItemData());
            }
        }

        return results;
    }

    public List<ItemData> findAnnotatedItemData(CrfFieldAnnotation annotation) {
        List<ItemData> results = new ArrayList<>();

        if (annotation != null && this.studyEventOid.equals(annotation.getEventDefinitionOid())) {
            if (this.formDataList != null) {
                for (FormData formData : this.formDataList) {
                    results.addAll(formData.findAnnotatedItemData(annotation));
                }
            }
        }

        return results;
    }

    public List<ItemData> findAnnotatedItemData(List<CrfFieldAnnotation> annotations) {
        List<ItemData> results = new ArrayList<>();

        if (annotations != null) {
            for (CrfFieldAnnotation annotation : annotations) {
                results.addAll(this.findAnnotatedItemData(annotation));
            }
        }

        return results;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */

    @Override
    public int compareTo(EventData o) {
        if (this.getStudyEventOid().equalsIgnoreCase(o.getStudyEventOid())) {

            if (this.getStudyEventRepeatKey() != null) {
                // null < any Integer
                if (o.getStudyEventRepeatKey() == null) return 1;

                if (Integer.valueOf(this.getStudyEventRepeatKey()) < Integer.valueOf(o.getStudyEventRepeatKey()))
                    return -1;
                if (Integer.valueOf(this.getStudyEventRepeatKey()) > Integer.valueOf(o.getStudyEventRepeatKey()))
                    return 1;
            }
            return 0;
        } else {
            return this.getStudyEventOid().compareTo(o.getStudyEventOid());
        }
    }

    //endregion

    //endregion

}
