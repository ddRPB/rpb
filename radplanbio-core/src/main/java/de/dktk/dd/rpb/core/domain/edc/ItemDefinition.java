/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * RPB item data field definition entity (field from eCRF)
 *
 * @author tomas@skripcak.net
 * @since 04 Jun 2013
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemDef")
public class ItemDefinition implements Identifiable<Integer>, Serializable, Comparable<ItemDefinition> {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(ItemDefinition.class);

    //endregion

    //region Members

    private Integer id; // unused for Transient entity

    @XmlAttribute(name="OID")
    private String oid;

    @XmlAttribute(name="ItemOID")
    private String itemOid;

    @XmlAttribute(name="Name")
    private String name; // item code name

    @XmlElement(name="Question")
    private Question question;

    @XmlElement(name="RangeCheck")
    private RangeCheck rangeCheck;

    private String label; // left text
    private String rightText; // right additional text

    @XmlAttribute(name="SASFieldName")
    private String sasFieldName;

    @XmlAttribute(name="Comment")
    private String description;

    @XmlAttribute(name="DataType")
    private String dataType;

    private Integer repeatItemRow;
    private String value;
    private String decodedValue; // if value is coded, decoded value represent human readable option

    @XmlAttribute(name="Length")
    private Integer length;
    private String units;

    private Boolean isPhi;
    private Boolean isRequired;

    private Boolean decode = false;

    @XmlElement(name = "CodeListRef")
    private CodeListDefinition codeListRef;
    private CodeListDefinition codeListDef;

    @XmlAttribute(name = "OrderNumber")
    private Integer OrderNumber;

    //region OpenClinica

    @XmlAttribute(name="FormOIDs", namespace = "http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String formOids;

    @XmlElement(name = "MultiSelectListRef", namespace = "http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private MultiSelectListDefinition multiSelectListRef;
    private MultiSelectListDefinition multiSelectListDef;

    @XmlElement(name = "ItemDetails", namespace = "http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private ItemDetails itemDetails;

    //endregion

    //region RadPlanBio

    @XmlTransient
    private ItemGroupDefinition itemGroupDefinition;

    //endregion

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public ItemDefinition() {
        // NOOP
    }

    public ItemDefinition(Boolean decode) {
        this.decode = decode;
    }

    //endregion

    //region Properties

    //region Id

    @XmlTransient
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

    //region CDISC ODM

    //region OID

    public String getOid() {
        return this.oid;
    }

    public void setOid(String value) {
        this.oid = value;
    }

    //endregion

    //region ItemOID

    public String getItemOid() {
        return this.itemOid;
    }

    public void setItemOid(String value) {
        this.itemOid = value;
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

    //region eCRF label

    public String getLabel() {
        // If OC extensions item details are available
        // needs context of the form, because the ItemDefinition can be present in several forms
        if (this.itemDetails != null && this.itemDetails.getItemPresentInForm() != null) {
            return this.itemDetails.getItemPresentInForm().get(0).getLeftItemText();
        }
        // Otherwise go for standard ODM question text
        else if (this.question != null) {
            return this.question.getTranslatedText();
        }
        return this.label;
    }

    public void setLabel(String value) {
        this.label = value;

        if (this.question != null) {
            this.question.setTranslatedText(value);
        }

        // needs context of the form, because the ItemDefinition can be present in several forms
        if (this.itemDetails != null && this.itemDetails.getItemPresentInForm() != null) {
            this.itemDetails.getItemPresentInForm().get(0).setLeftItemText(value);
        }
    }

    //endregion

    //region Question

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question valueObject) {
        this.question = valueObject;
    }

    //endregion

    //region RangeCheck

    public RangeCheck getRangeCheck() {
        return this.rangeCheck;
    }

    public void setRangeCheck(RangeCheck rangeCheck) {
        this.rangeCheck = rangeCheck;
    }

    //endregion

    //region RightText

    public String getRightText() {
        return this.rightText;
    }

    public void setRightText(String value) {
        this.rightText = value;
    }

    //endregion

    //region Description

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    //endregion

    //region SAS Field name

    public String getSasFieldName() {
        return this.sasFieldName;
    }

    public void setSasFieldName(String value) {
        this.sasFieldName = value;
    }

    //endregion

    //region DataType

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String value) {
        this.dataType = value;
    }

    //endregion

    //region RepeatItemRow

    public Integer getRepeatItemRow() {
        return this.repeatItemRow;
    }

    public void setRepeatItemRow(Integer value) {
        this.repeatItemRow = value;
    }

    //endregion

    public Integer getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        OrderNumber = orderNumber;
    }


    //region Value

    public String getValue() {
        // PHI items will display value PHI
        String result = "PHI";

        // Only get value of non PHI items
        if (this.isPhi != null) {
            if (!this.isPhi) {
                result = this.value;
            }
        }
        else {
            result = this.value;
        }

        // Optionally overwrite with decoded value corresponding to item value code
        if (this.decode != null && this.decode && this.decodedValue != null && !this.decodedValue.equals("")) {
            result = this.decodedValue;
        }

        return result;
    }

    public void setValue(String value) {
        this.value = value;
    }

    //endregion

    //region DecodedValue

    public String getDecodedValue() {
        return this.decodedValue;
    }

    public void setDecodedValue(String value) {
        this.decodedValue = value;
    }

    //endregion

    //region Length

    public Integer getLength() {
        return this.length;
    }

    public void setLength(Integer value) {
        this.length = value;
    }

    //endregion

    //region Units

    public String getUnits() {
        return this.units;
    }

    public void setUnits(String value) {
        this.units = value;
    }

    //endregion

    //region isPhi

    public Boolean getIsPhi() {

        if (this.isPhi != null) {
            return this.isPhi;
        }
        else if (this.itemDetails != null && this.itemDetails.getItemPresentInForm() != null) {
            List<ItemPresentInForm> itemPresentInFormList = this.itemDetails.getItemPresentInForm();

            for (ItemPresentInForm item: itemPresentInFormList){

                if (item.getPhi().equals(Constants.OC_YES)) return Boolean.TRUE;

            }

            return Boolean.FALSE;
        }

        return null;
    }

    public void setIsPhi(Boolean value) {
        this.isPhi = value;
    }

    //endregion

    //region isRequired

    public Boolean getIsRequired() {
        if (this.isPhi != null) {
            return this.isRequired;
        }

        return null;
    }

    public void setIsRequired(Boolean value) {
        this.isRequired = value;
    }

    //endregion

    //region isShown

    public Boolean getIsShown() {
        if (this.itemDetails != null && this.itemDetails.getItemPresentInForm() != null) {

            List<ItemPresentInForm> itemPresentInFormList = this.itemDetails.getItemPresentInForm();

            for (ItemPresentInForm item: itemPresentInFormList){
                if (item.getShowItem().equals(Constants.OC_YES)) return Boolean.TRUE;
            }

            return Boolean.FALSE;
        }

        return null;
    }

    //endregion

    //region CodeList

    public CodeListDefinition getCodeListRef() {
        return this.codeListRef;
    }

    public void setCodeListRef(CodeListDefinition codeList) {
        this.codeListRef = codeList;
    }

    public CodeListDefinition getCodeListDef() {
        return this.codeListDef;
    }

    public void setCodeListDef(CodeListDefinition codeList) {
        this.codeListDef = codeList;
    }

    //endregion

    //endregion

    //endregion

    //region OpenClinica

    public String getFormOids() {
        return formOids;
    }

    public void setFormOids(String formOids) {
        this.formOids = formOids;
    }


    //region MultiSelectList

    public MultiSelectListDefinition getMultiSelectListRef() {
        return this.multiSelectListRef;
    }

    public void setMultiSelectListRef(MultiSelectListDefinition listRef) {
        this.multiSelectListRef = listRef;
    }

    public MultiSelectListDefinition getMultiSelectListDef() {
        return this.multiSelectListDef;
    }

    public void setMultiSelectListDef(MultiSelectListDefinition listDef) {
        this.multiSelectListDef = listDef;
    }

    //endregion

    //region ItemDetails

    public ItemDetails getItemDetails() {
        return this.itemDetails;
    }

    public void setItemDetails(ItemDetails details) {
        this.itemDetails = details;
    }

    //endregion

    //endregion

    //region RadPlanBio

    public ItemGroupDefinition getItemGroupDefinition() {
        return this.itemGroupDefinition;
    }

    public void setItemGroupDefinition(ItemGroupDefinition itemGroupDefinition) {
        this.itemGroupDefinition = itemGroupDefinition;
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof ItemDefinition && hashCode() == other.hashCode());
    }

    /**
     * Generate entity hash code
     * @return hash
     */
    @Override
    public int hashCode() {
        String alternateIdentifier = "";
        if (this.oid != null && !this.oid.equals("")) {
            alternateIdentifier = this.oid;
        }
        else if (this.itemOid != null && !this.itemOid.equals("")) {
            alternateIdentifier = this.itemOid;
        }

        return identifiableHashBuilder.hash(log, this, alternateIdentifier);
    }

    /**
     * Construct a readable string representation for this entity instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("oid", this.oid)
                .add("name", this.name)
                .add("label", this.label)
                .add("rightText", this.rightText)
                .add("description", this.description)
                .add("dataType", this.dataType)
                .add("repeatItemRow", this.repeatItemRow)
                .add("value", this.value)
                .add("length", this.length)
                .add("units", this.units)
                .add("isPhi", this.isPhi)
                .add("isRequired", this.isRequired)
                .toString();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param itemDefinition the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     *                            from being compared to this object.
     */
    @Override
    public int compareTo(ItemDefinition itemDefinition) {

        try {
            if (this.getOrderNumber() < itemDefinition.getOrderNumber()) {
                return -1;
            }
            if (this.getOrderNumber() > itemDefinition.getOrderNumber()) {
                return 1;
            }
        } catch (NullPointerException e) {
            // null value goes is less
            if (this.getOrderNumber() == null) {
                return -1;
            }
            if (itemDefinition.getOrderNumber() == null) {
                return 1;
            }
        }
        // else values are equal or both null
        return 0;

    }

    //endregion

    //region Methods

    public Boolean isCoded() {
        return this.codeListDef != null || this.multiSelectListDef != null;
    }

    public Boolean isMultiCoded() {
        return this.multiSelectListDef != null;
    }

    public Boolean isDate() {
        if (this.dataType != null) {
            return this.dataType.equals("date") || this.dataType.equals("partialDate") || this.dataType.equals("pdate");
        }
        else {
            return Boolean.FALSE;
        }
    }

    public Boolean isNumber() {
        if (this.dataType != null) {
            return this.dataType.equals("integer") || this.dataType.equals("float");
        }
        else {
            return Boolean.FALSE;
        }
    }

    public List<ListItem> getListItems() {
        List<ListItem> results = new ArrayList<ListItem>();

        if (this.getCodeListDef() != null) {
            for (ListItem li : this.getCodeListDef().getCodeListItems()) {
                results.add(li);
            }

        }
        else if (this.getMultiSelectListDef() != null) {
            for (ListItem li : this.getMultiSelectListDef().getMultiSelectListItems()) {
                results.add(li);
            }
        }

        return results;
    }

    public Boolean isCodeValid(String targetValue) {
        Boolean result = false;

        if (this.codeListDef != null) {
            for (CodeListItem cli : this.codeListDef.getCodeListItems()) {
                if (cli.getCodedValue().equals(targetValue)) {
                    result = true;
                    break;
                }
            }
        }
        else if (this.multiSelectListDef != null) {
            for (MultiSelectListItem msli : this.multiSelectListDef.getMultiSelectListItems()) {
                if (msli.getCodedValue().equals(targetValue)) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    public Boolean isPresentInForm(String formOid) {
        if (this.itemDetails != null && this.itemDetails.getItemPresentInForm() != null) {

            List<ItemPresentInForm> itemPresentInFormList = this.itemDetails.getItemPresentInForm();

            for (ItemPresentInForm item: itemPresentInFormList){

                if (item.getFormOid().equals(formOid)) return Boolean.TRUE;

            }

        }

        return Boolean.FALSE;
    }

    //endregion

}
