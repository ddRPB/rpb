/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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

package de.dktk.dd.rpb.core.domain.edc.mapping;

import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

/**
 * MappedCsvItem domain entity
 *
 * @author tomas@skripcak.net
 * @since 03 Mar 2015
 */
@Entity
@DiscriminatorValue("CSV")
public class MappedCsvItem extends AbstractMappedItem {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(MappedCsvItem.class);

    //endregion

    //region Members

    private String header;

    //endregion

    //region Constructors

    public MappedCsvItem() {
        // NOOP
    }

    public MappedCsvItem(String header) {
        // The column name should be free from starting and trailing spaces
        this.header = header.trim();
        this.label = header.trim();
    }

    //endregion

    //region Properties

    @Size(max = 255)
    @Column(name = "HEADER")
    public String getHeader() {
        return this.header;
    }

    public void setHeader(String value) {
        this.header = value;
        this.label = header;
    }

    //endregion

    //region Overrides

    /**
     * Construct a readable string representation for this entity instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("label", this.label)
                .add("type", "CSV")
                .add("header", this.header)
                .toString();
    }

    //endregion

}
