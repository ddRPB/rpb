/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2023 RPB Team
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

package de.dktk.dd.rpb.core.repository.rpb;

import de.dktk.dd.rpb.core.dao.rpb.RadPlanBioDataDao;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

/**
 * Default implementation of the {@link IRadPlanBioDataRepository} interface.
 *
 * @author tomas@skripcak.net
 * @see IRadPlanBioDataRepository
 * <p>
 * RadPlanBioDataRepository
 */
@Named
@Singleton
public class RadPlanBioDataRepository implements IRadPlanBioDataRepository {

    //region Logging

    private static final Logger log = LoggerFactory.getLogger(RadPlanBioDataRepository.class);

    //endregion

    //region Injects

    protected RadPlanBioDataDao dao;

    @Inject
    public void setRadPlanBioDataDao(RadPlanBioDataDao dao) {
        this.dao = dao;
    }

    //endregion

    //region DefaultAccount

    @Override
    @Transactional(readOnly = true)
    public String getDefaultAccountUsernameByApiKey(String accountApiKey) {
        return this.dao.getDefaultAccountUsernameByApiKey(accountApiKey);
    }

    //endregion

    // region API Key

    @Override
    @Transactional(readOnly = true)
    public String getDefaultAccountApiKeyByUsername(String userName) {
        return this.dao.getDefaultAccountApiKeyByUsername(userName);
    }

    //endregion

    //region Pacs

    @Override
    @Transactional(readOnly = true)
    public String getPacsUrlByAccountApiKey(String accountApiKey) {
        return this.dao.getPacsUrlByAccountApiKey(accountApiKey);
    }

    //endregion
    @Override
    @Transactional(readOnly = true)
    public List<Person> getPersonsWithMatchingName(String searchString, int maxResults){
        return this.dao.getPersonsWithMatchingName(searchString, maxResults);

    }


}
