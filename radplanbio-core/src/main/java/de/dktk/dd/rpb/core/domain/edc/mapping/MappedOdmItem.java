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

package de.dktk.dd.rpb.core.domain.edc.mapping;

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
    private String studyEventRepeatKey;
    private String formOid;
    private String itemGroupOid;
    private String itemGroupRepeatKey;
    private String itemOid;

    //endregion

    //region Constructors

    public MappedOdmItem() {
        // NOOP
    }

    //endregion

    //region Properties

    @Size(max = 255)
    @Column(name = "STUDYEVENTOID", length = 255)
    public String getStudyEventOid() {
        return this.studyEventOid;
    }

    public void setStudyEventOid(String oid) {
        this.studyEventOid = oid;
    }

    @Size(max = 255)
    @Column(name = "SEREPEATKEY", length = 255)
    public String getStudyEventRepeatKey() {
        return this.studyEventRepeatKey;
    }

    public void setStudyEventRepeatKey(String repeatKey) {
        this.studyEventRepeatKey = repeatKey;
    }

    @Size(max = 255)
    @Column(name = "FORMOID", length = 255)
    public String getFormOid() {
        return this.formOid;
    }

    public void setFormOid(String oid) {
        this.formOid = oid;
    }

    @Size(max = 255)
    @Column(name = "ITEMGROUPOID", length = 255)
    public String getItemGroupOid() {
        return this.itemGroupOid;
    }

    public void setItemGroupOid(String oid) {
        this.itemGroupOid = oid;
    }

    @Size(max = 255)
    @Column(name = "IGREPEATKEY", length = 255)
    public String getItemGroupRepeatKey() {
        return this.itemGroupRepeatKey;
    }

    public void setItemGroupRepeatKey(String repeatKey) {
        this.itemGroupRepeatKey = repeatKey;
    }

    @Size(max = 255)
    @Column(name = "ITEMOID", length = 255)
    public String getItemOid() {
        return this.itemOid;
    }

    public void setItemOid(String oid) {
        this.itemOid = oid;
        this.label = oid;
    }

    //endregion

}
