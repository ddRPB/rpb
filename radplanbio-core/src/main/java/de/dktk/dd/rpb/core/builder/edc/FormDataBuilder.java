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

package de.dktk.dd.rpb.core.builder.edc;

import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupData;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.apache.log4j.Logger;

/**
 * Builder for the FormData object,
 * which can be used to create the FormData part of an ODM-XML object.
 */
public class FormDataBuilder {
    //region Finals

    private static final Logger log = Logger.getLogger(FormDataBuilder.class);
    private final FormData formData = new FormData();

    // endregion

    //region constructor

    private FormDataBuilder() {
    }

    public static FormDataBuilder getInstance() {
        return new FormDataBuilder();
    }

    // endregion

    /**
     * Building the FormData instance out of the current configuration properties.
     *
     * @return FormData
     */
    public FormData build() throws MissingPropertyException {
        if (this.formData.getFormOid() == null || this.formData.getFormOid().isEmpty()) {
            throw new MissingPropertyException("FormOid is missing");
        }
        return this.formData;
    }

    /**
     * Setting the formOid property for the FormData object
     *
     * @param formOid String
     * @return FormDataBuilder
     */
    public FormDataBuilder setFormOid(String formOid) {
        this.formData.setFormOid(formOid);
        return this;
    }

    /**
     * Adding an additional ItemGroupData object to FormData
     *
     * @param itemGroupData ItemGroupData
     * @return FormDataBuilder
     */
    public FormDataBuilder addItemGroupData(ItemGroupData itemGroupData) {
        this.formData.addItemGroupData(itemGroupData);
        return this;
    }
}