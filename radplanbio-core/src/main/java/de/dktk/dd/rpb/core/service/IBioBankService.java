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

package de.dktk.dd.rpb.core.service;

import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;

public interface IBioBankService {

    //region Setup

    void setupConnection(String url, String user, String password);

    //endregion

    //region Hello

    boolean hello();

    //endregion

    //region Patient

    Person loadPatient(String mpi);

    Person loadPatient(String idType, String patientId);

    Person loadPatient(String studyProtocolId, String idType, String id);

    boolean createPatient(String ssidType, StudySubject studySubject, PartnerSite site, boolean isPrincipalSiteSubject, boolean useMPI, boolean useIDAT);

    //endregion

    //region Episode

    void loadEpisode(String id);

    //endregion

    //region Sample

    void loadSample(String id);

    //endregion

}