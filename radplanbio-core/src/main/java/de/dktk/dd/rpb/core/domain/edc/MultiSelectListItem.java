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

import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import org.apache.log4j.Logger;

import javax.xml.bind.annotation.*;

/**
 * MultiSelectListItem CDISC ODM entity
 *
 * @author tomas@skripcak.net
 * @since 03 March 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="MultiSelectListItem", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
public class MultiSelectListItem extends ListItem {

    //region Finals

    private static final Logger log = Logger.getLogger(MultiSelectListItem.class);

    //endregion

    //region Members

    @XmlTransient
    private Integer id;

    @XmlAttribute(name="CodedOptionValue")
    private String codedOptionValue;

    @XmlElement(name="Decode", namespace="http://www.cdisc.org/ns/odm/v1.3")
    private Decode decode;

    //endregion

    //region Properties

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    public boolean isIdSet() {
        return this.id != null;
    }

    @Override
    public String getCodedValue() {
        return this.codedOptionValue;
    }

    @Override
    public void setCodedValue(String value) {
        this.codedOptionValue = value;
    }

    @SuppressWarnings("unused")
    public String getCodedOptionValue() {
        return this.codedOptionValue;
    }

    @SuppressWarnings("unused")
    public void setCodedOptionValue(String value) {
        this.codedOptionValue = value;
    }

    @SuppressWarnings("unused")
    public Decode getDecode() {
        return this.decode;
    }

    @SuppressWarnings("unused")
    public void setDecode(Decode value) {
        this.decode = value;
    }

    @Override
    public String getDecodedText() {
        if (this.decode != null) {
            return this.decode.getTranslatedText();
        }
        else {
            return null;
        }
    }

    @Override
    public void setDecodedText(String value) {
        if (this.decode != null) {
            this.decode.setTranslatedText(value);
        }
    }

    //endregion

    //region Overrides

    /**
     * equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof MultiSelectListItem && hashCode() == other.hashCode());
    }

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    //endregion

}
