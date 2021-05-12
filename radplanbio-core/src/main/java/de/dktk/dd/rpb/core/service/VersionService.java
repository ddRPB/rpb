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

package de.dktk.dd.rpb.core.service;

import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;

import static org.apache.commons.lang.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

@Named
public class VersionService {

    //region Finals

    private static final String NO_VCS_REVISION = "noVCSRevision";

    //endregion

    //region Members

    @Value("${build.version:}")
    private String version;

    @Value("${build.vcsrevision:}")
    private String vcsRevision;

    //endregion

    //region Properties

    @SuppressWarnings("unused")
    public String getVersion() {
        return version;
    }

    @SuppressWarnings("unused")
    public String getVcsRevision() {
        return vcsRevision;
    }

    //endregion

    //region Methods

    public String format() {
        if (endsWithIgnoreCase(version, "SNAPSHOT") && !equalsIgnoreCase(vcsRevision, NO_VCS_REVISION)) {
            return "Version: "+ version + " (Changeset: " + vcsRevision + ")";
        }

        return version;
    }

    //endregion

}
