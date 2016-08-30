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
import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import de.dktk.dd.rpb.core.domain.edc.FormDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupDefinition;
import org.apache.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

/**
 * MappedOdmItem domain entity
 * ODM (Operation Data Model XML) as mapping target
 *
 * @author tomas@skripcak.net
 * @since 03 March 2015
 */
@Entity
@DiscriminatorValue("ODM")
public class MappedOdmItem extends AbstractMappedItem {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(MappedOdmItem.class);

    //endregion

    //region Members

    private String studyEventOid;
    private String formOid;
    private String itemGroupOid;
    private String itemOid;

    //endregion

    //region Constructors

    public MappedOdmItem() {
        // NOOP
    }

    public MappedOdmItem(String itemOid) {
        this.itemOid = itemOid;

        // Label is used as row key
        this.label = itemOid;
    }

    public MappedOdmItem(EventDefinition eventDefinition, FormDefinition formDefinition, ItemGroupDefinition itemGroupDefinition, ItemDefinition itemDefinition) {
        this.studyEventOid = eventDefinition.getOid();
        this.formOid = formDefinition.getOid();
        this.itemGroupOid = itemGroupDefinition.getOid();
        this.itemOid = itemDefinition.getOid();

        // Label is used as row key
        this.label = itemDefinition.getOid();
    }

    //endregion

    //region Properties

    @Size(max = 255)
    @Column(name = "STUDYEVENTOID")
    public String getStudyEventOid() {
        return this.studyEventOid;
    }

    public void setStudyEventOid(String oid) {
        this.studyEventOid = oid;
    }

    @Size(max = 255)
    @Column(name = "FORMOID")
    public String getFormOid() {
        return this.formOid;
    }

    public void setFormOid(String oid) {
        this.formOid = oid;
    }

    @Size(max = 255)
    @Column(name = "ITEMGROUPOID")
    public String getItemGroupOid() {
        return this.itemGroupOid;
    }

    public void setItemGroupOid(String oid) {
        this.itemGroupOid = oid;
    }

    @Size(max = 255)
    @Column(name = "ITEMOID")
    public String getItemOid() {
        return this.itemOid;
    }

    public void setItemOid(String oid) {
        this.itemOid = oid;
        this.label = oid;
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
                .add("type", "ODM")
                .add("event", this.studyEventOid)
                .add("form", this.formOid)
                .add("itemGroup", this.itemGroupOid)
                .add("item", this.itemOid)
                .toString();
    }

    //endregion

}