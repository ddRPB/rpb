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

package de.dktk.dd.rpb.core.repository.admin.ctms;

import de.dktk.dd.rpb.core.dao.ctms.CurriculumVitaeItemTypeDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.CurriculumVitaeItemType;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * CurriculumVitaeItemTypeRepository
 *
 * Default implementation of the {@link ICurriculumVitaeItemTypeRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see ICurriculumVitaeItemTypeRepository
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("curriculumVitaeItemTypeRepository")
@Singleton
public class CurriculumVitaeItemTypeRepository extends RepositoryImpl<CurriculumVitaeItemType, Integer> implements ICurriculumVitaeItemTypeRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(CurriculumVitaeItemTypeRepository.class);

    //endregion

    //region Injects

    protected CurriculumVitaeItemTypeDao dao;

    @Inject
    public void setTypeDao(CurriculumVitaeItemTypeDao value) {
        this.dao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<CurriculumVitaeItemType, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CurriculumVitaeItemType getNew() {
        return new CurriculumVitaeItemType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CurriculumVitaeItemType getNewWithDefaults() {
        CurriculumVitaeItemType result = getNew();
        result.initDefaultValues();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CurriculumVitaeItemType get(CurriculumVitaeItemType model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getName())) {
            CurriculumVitaeItemType result = this.getByName(model.getName());
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
    public CurriculumVitaeItemType getByName(String name) {
        CurriculumVitaeItemType entity = new CurriculumVitaeItemType();
        entity.setName(name);

        return findUniqueOrNone(entity);
    }

    //endregion

}
