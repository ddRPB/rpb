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
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import org.apache.log4j.Logger;
import javax.xml.bind.annotation.*;

/**
 * CodeListItem CDISC ODM domain entity
 *
 * @author tomas@skripcak.net
 * @since 03 Mar 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="CodeListItem")
public class CodeListItem extends ListItem {

    //region Finals

    private static final Logger log = Logger.getLogger(CodeListItem.class);

    //endregion

    //region Members

    @XmlTransient
    private Integer id;

    @XmlAttribute(name="CodedValue")
    private String codedValue;

    @XmlElement(name="Decode")
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
        return this.codedValue;
    }

    @Override
    public void setCodedValue(String codedValue) {
        this.codedValue = codedValue;
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
        return this.decode.getTranslatedText();
    }

    @Override
    public void setDecodedText(String value) {
        this.decode.setTranslatedText(value);
    }

    //endregion

    //region Overrides

    /**
     * equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof CodeListItem && hashCode() == other.hashCode());
    }

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this enity instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("code", this.codedValue)
                .add("decode", this.getDecodedText())
                .toString();
    }

    //endregion

}