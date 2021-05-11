/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
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

package de.dktk.dd.rpb.core.repository.admin.ctms;

import de.dktk.dd.rpb.core.dao.ctms.SponsoringTypeDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.SponsoringType;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * SponsoringTypeRepository
 *
 * Default implementation of the {@link ISponsoringTypeRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see ISponsoringTypeRepository
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("sponsoringTypeRepository")
@Singleton
public class SponsoringTypeRepository extends RepositoryImpl<SponsoringType, Integer> implements ISponsoringTypeRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(SponsoringTypeRepository.class);

    //endregion

    //region Injects

    protected SponsoringTypeDao sponsoringTypeDao;

    @Inject
    public void setTypeDao(SponsoringTypeDao value) {
        this.sponsoringTypeDao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<SponsoringType, Integer> getDao() {
        return this.sponsoringTypeDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SponsoringType getNew() {
        return new SponsoringType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SponsoringType getNewWithDefaults() {
        SponsoringType result = getNew();
        result.initDefaultValues();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public SponsoringType get(SponsoringType model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getName())) {
            SponsoringType result = this.getByName(model.getName());
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public SponsoringType getByName(String name) {
        SponsoringType entity = new SponsoringType();
        entity.setName(name);

        return findUniqueOrNone(entity);
    }

    //endregion

}