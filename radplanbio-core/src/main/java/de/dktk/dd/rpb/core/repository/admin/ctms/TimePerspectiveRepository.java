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

import de.dktk.dd.rpb.core.dao.ctms.TimePerspectiveDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.TimePerspective;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * TimePerspectiveRepository
 *
 * Default implementation of the {@link ITimePerspectiveRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see ITimePerspectiveRepository
 *
 * @author tomas@skripcak.net
 * @since 30 Aug 2017
 */
@Named("timePerspectiveRepository")
@Singleton
public class TimePerspectiveRepository extends RepositoryImpl<TimePerspective, Integer> implements ITimePerspectiveRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TimePerspectiveRepository.class);

    //endregion

    //region Injects

    protected TimePerspectiveDao timePerspectiveDao;

    @Inject
    public void setTimePerspectiveDao(TimePerspectiveDao value) {
        this.timePerspectiveDao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<TimePerspective, Integer> getDao() {
        return this.timePerspectiveDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimePerspective getNew() {
        return new TimePerspective();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimePerspective getNewWithDefaults() {
        TimePerspective result = getNew();
        result.initDefaultValues();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public TimePerspective get(TimePerspective model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getName())) {
            TimePerspective result = this.getByName(model.getName());
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
    public TimePerspective getByName(String name) {
        TimePerspective entity = new TimePerspective();
        entity.setName(name);

        return findUniqueOrNone(entity);
    }

    //endregion

}
