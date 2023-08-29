/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
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

package de.dktk.dd.rpb.core.domain.pacs;

import com.google.common.base.Objects;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * RPB DICOM Image domain object to encapsulate data coming from PACS
 *
 * @author tomas@skripcak.net
 * @since 26 Nov 2015
 */
@XmlRootElement
public class DicomImage implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(DicomImage.class);

    //endregion

    //region Members

    private Integer id; // unused for Transient entity

    private String sopInstanceUID;
    private Integer size;

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public DicomImage() {
        // NOOP
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

    //region SopInstanceUid

    public String getSopInstanceUID() {
        return this.sopInstanceUID;
    }

    public void setSopInstanceUID(String sopInstanceUid) {
        this.sopInstanceUID = sopInstanceUid;
    }

    //endregion

    //region Size

    public Integer getSize() {
        return this.size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    //endregion

    //endregion

    // region methods

    /**
     * Extracts the DicomSeries UID if the DICOM Image references another Series
     * Will be overwritten in Subclasses
     *
     * @return String Dicom UID of the referenced series if available otherwise an empty String will be returned
     */
    public String getReferencedDicomSeriesUID() {
        return null;
    }

    public List<String> getMetaParameterList() {
        return new ArrayList<>();
    }

    public String getDescription() {
        return "";
    }

    // endregion

    //region Overrides

    /**
     * equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof DicomImage && this.hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this, this.sopInstanceUID);
    }

    /**
     * Construct a readable string representation for this Study instance.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("sopInstanceUid", this.sopInstanceUID) //
                .toString();
    }

    //endregion

}
