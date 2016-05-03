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

package de.dktk.dd.rpb.core.dao.ctms;

import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.TimeLineEvent;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * JPA 2 Data Access Object for {@link TimeLineEvent}.
 * Note: do not use @Transactional in the DAO layer.
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
@Named
@Singleton
public class TimeLineEventDao extends GenericDao<TimeLineEvent, Integer> {

    //region Constructors

    public TimeLineEventDao() {
        super(TimeLineEvent.class);
    }

    //endregion

}
