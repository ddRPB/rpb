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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.List;

/**
 * RPB item code list definition (CDISC ODM)
 *
 * @author tomas@skripcak.net
 * @since 04 Jun 2013
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="CodeList")
public class CodeListDefinition implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(CodeListDefinition.class);

    //endregion

    //region Members

    //region Reference

    @XmlAttribute(name="CodeListOID")
    private String codeListOid;

    //endregion

    //region Definition

    @XmlAttribute(name="OID")
    private String oid;

    @XmlAttribute(name="Name")
    private String name;

    @XmlAttribute(name="DataType")
    private String dataType;

    @XmlAttribute(name="SASFormatName")
    private String sasFormatName;

    @XmlElement(name="CodeListItem")
    private List<CodeListItem> codeListItems;

    //endregion

    //endregion

    //region Properties

    //region Reference

    public String getCodeListOid() {
        return this.codeListOid;
    }

    public void setCodeListOid(String oid) {
        this.codeListOid = oid;
    }

    //endregion

    //region Definition

    public String getOid() {
        return this.oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getSasFormatName() {
        return this.sasFormatName;
    }

    public void setSasFormatName(String value) {
        this.sasFormatName = value;
    }

    public List<CodeListItem> getCodeListItems() {
        return this.codeListItems;
    }

    public void setCodeListItems(List<CodeListItem> list) {
        this.codeListItems = list;
    }

    //endregion

    //endregion

    /**
     * Returns the first decoded value from the CodeItem list where the coded value String matches
     * @param codedValue String coded value
     * @return String decoded value
     */
    public String getDecodedText(String codedValue){
        if(codedValue == null){
            return null;
        }

        for(CodeListItem codeListItem:this.codeListItems){
            if(codeListItem.getCodedValue() != null){
                if(codedValue.equals(codeListItem.getCodedValue())){
                    return codeListItem.getDecodedText();
                }
            }
        }

        // empty string if codedValue does not match
        return "";
    }



}
