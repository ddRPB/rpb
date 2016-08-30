/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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

package de.dktk.dd.rpb.core.domain;

import java.io.Serializable;

/**
 * By making entities implement this interface we can easily retrieve from the
 * {@link de.dktk.dd.rpb.core.dao.support.GenericDao} the identifier property of the entity.
 */
public interface Identifiable<PK extends Serializable> {

    /** Provides the primary key
     * @return the primary key
     */
    PK getId();

    /**
     * Sets the primary key
     */
    void setId(PK id);

    /**
     * Helper method to know whether the primary key is set or not.
     * It means that we can determine whether there is necessary:
     * to persist (Entity is transactional, Id is null, insert should be performed on DB site)
     * or to merge (Entity is persisted, id is not null, the update operation will be performed on DB site)
     *
     * @return true if the primary key is set, false otherwise
     */
    boolean isIdSet();

}