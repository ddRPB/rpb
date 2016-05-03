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

import de.dktk.dd.rpb.core.dao.edc.OpenClinicaDataDao;
import de.dktk.dd.rpb.core.domain.edc.DataQueryResult;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;

import de.dktk.dd.rpb.core.domain.edc.Study;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of the {@link IOpenClinicaDataRepository} interface.
 * @see IOpenClinicaDataRepository
 *
 * OCDataRepository
 *
 * @author tomas@skripcak.net
 * @since 14 Jul 2013
 */
@Named("openClinicaDataRepository")
@Singleton
public class OpenClinicaDataRepository implements IOpenClinicaDataRepository {

    //region Logging

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(OpenClinicaDataRepository.class);

    //endregion

    //region Injects

    protected OpenClinicaDataDao dao;

    @Inject
    public void setOpenClinicaDataDao(OpenClinicaDataDao dao) {
        this.dao = dao;
    }

    //endregion

    //region Overrides

    //region UserAccount

    @Transactional
    public String getUserAccountHash(String username) {
        return this.dao.getUserAccountHash(username);
    }

    //endregion

    //region Study

    @Transactional
    public Study getStudyByIdentifier(String identifier) {
        return this.dao.getStudyByIdentifier(identifier);
    }

    @Transactional
    public Study getUserActiveStudy(String username) {
        return this.dao.getUserActiveStudy(username);
    }

    public boolean changeUserActiveStudy(String username, Study newActiveStudy) {
        return this.dao.changeUserActiveStudy(username, newActiveStudy);
    }

    //endregion

    //region CRF item values

    @Override
    @Transactional(readOnly = true)
    public List<ItemDefinition> getData(String uniqueIdentifier) {
        return this.dao.findAllItemDataForStudy(uniqueIdentifier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataQueryResult> getData(String uniqueIdentifier, List<ItemDefinition> query, Boolean decodeItemValues) {
        return this.dao.findItemData(uniqueIdentifier, query, decodeItemValues);
    }

    //endregion

    //endregion

}
