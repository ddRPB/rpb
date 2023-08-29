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

package de.dktk.dd.rpb.core.repository.ctms;

import de.dktk.dd.rpb.core.dao.ctms.OrganisationDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.Organisation;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * OrganisationRepository
 *
 * Default implementation of the {@link IOrganisationRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IOrganisationRepository
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("organisationRepository")
@Singleton
public class OrganisationRepository extends RepositoryImpl<Organisation, Integer> implements IOrganisationRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(OrganisationRepository.class);

    //endregion

    //region Injects

    protected OrganisationDao dao;

    @Inject
    public void setDao(OrganisationDao value) {
        this.dao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<Organisation, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Organisation getNew() {
        return new Organisation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Organisation getNewWithDefaults() {
        Organisation result = getNew();
        result.initDefaultValues();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Organisation get(Organisation model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        return null;
    }

    //endregion

}
