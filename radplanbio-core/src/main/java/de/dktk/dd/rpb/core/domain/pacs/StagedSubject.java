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

import de.dktk.dd.rpb.core.domain.edc.Subject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a subject that has been staged in PACS for research
 */
public class StagedSubject extends Subject {

    private List<StagedDicomStudy> stagedStudies;

    public StagedSubject() {
        super();
    }

    public List<StagedDicomStudy> getStagedStudies() {
        return stagedStudies;
    }

    public void setStagedStudies(List<StagedDicomStudy> stagedStudies) {
        // sort by date for the data table view
        Collections.sort(stagedStudies, new Comparator<StagedDicomStudy>() {

            @Override
            public int compare(StagedDicomStudy o1, StagedDicomStudy o2) {
                return o1.getDateStudy().compareTo(o2.getDateStudy());
            }
        });

        this.stagedStudies = stagedStudies;
    }
}
