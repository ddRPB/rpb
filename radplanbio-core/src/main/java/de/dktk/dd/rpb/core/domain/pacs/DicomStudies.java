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

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * RPB DICOM Studies domain object wrapper to encapsulate DICOM study list data coming from PACS
 *
 * @author tomas@skripcak.net
 * @since 12 April 2017
 */
@XmlRootElement(name = "DicomStudies")
public class DicomStudies {

    private List<DicomStudy> dicomStudies;

    public List<DicomStudy> getDicomStudies() {
        return this.dicomStudies;
    }

    public void setDicomStudies(List<DicomStudy> list) {
        this.dicomStudies = list;
    }

}