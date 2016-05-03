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

import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import org.apache.log4j.Logger;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DataQueryResult domain entity (transient)
 *
 * Data custom radiotherapy research data queries executed via RadPlanBio produces unstructured query result data which is
 * stored within this entity. The query is always associated with specific StudySubject (no data exists without this associations).
 *
 * @author tomas@skripcak.net
 * @since 09 Dec 2014
 */
public class DataQueryResult implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(DataQueryResult.class);

    //endregion

    //region Members

    private Integer id;
    private StudySubject studySubject;
    private List<ItemDefinition> dataItems;

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public DataQueryResult() {
        this.dataItems = new ArrayList<ItemDefinition>();
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

    //region StudySubject

    public StudySubject getStudySubject() {
        return this.studySubject;
    }

    public void setStudySubject(StudySubject entity) {
        this.studySubject = entity;
    }

    //endregion

    //region DataItems

    public List<ItemDefinition> getDataItems() {
        return  this.dataItems;
    }

    public void setDataItems(List<ItemDefinition> list) {
        this.dataItems = list;
    }

    public boolean addDataItem(ItemDefinition item) {
        return item != null && this.dataItems.add(item);
    }

    public boolean removeDataItem(ItemDefinition item) {
        return this.dataItems != null && this.dataItems.remove(item);
    }

    @SuppressWarnings("unused")
    public boolean containsDataItem(ItemDefinition item) {
        return this.dataItems != null && this.dataItems.contains(item);
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof DataQueryResult && hashCode() == other.hashCode());
    }

    /**
     * Generate entity hash code
     * @return hash
     */
    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this RtStructType instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .toString();
    }

    //endregion

    //region Methods

    public String getItemData(String item) {
        String value = "";
        for (ItemDefinition id : this.dataItems) {
            if (id.getOid().equals(item)) {
                // If the item is in repeating group put the values in a list
                if (!value.equals("")) {
                    value += "; ";
                }
                value += id.getValue();
            }
        }

        return value;
    }


    //endregion

}
