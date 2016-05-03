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

package de.dktk.dd.rpb.core.repository.edc;

import java.util.List;

import de.dktk.dd.rpb.core.domain.edc.DataQueryResult;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.Study;

/**
 * OpenClinica Database Repository - operation layer abstraction
 *
 * @author tomas@skripcak.net
 * @since 05 Jun 2013
 */
public interface IOpenClinicaDataRepository {

    //region Methods

    //region UserAccount

    String getUserAccountHash(String username);

    //endregion

    //region Study

    Study getStudyByIdentifier(String identifier);

    Study getUserActiveStudy(String username);

    boolean changeUserActiveStudy(String username, Study newActiveStudy);

    //endregion

    //region CRF item values

    List<ItemDefinition> getData(String uniqueIdentifier);

    List<DataQueryResult> getData(String uniqueIdentifier, List<ItemDefinition> query, Boolean decodeItemValues);

    //endregion

    //endregion
}
