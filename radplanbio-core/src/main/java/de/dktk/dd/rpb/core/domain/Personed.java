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

package de.dktk.dd.rpb.core.domain;

/**
 * By making entities implement this interface we can easily sort them as they are implementing surname and firstname properties
 */
public interface Personed {

    /**
     * Provides the firstname
     * @return the entity firstname
     */
    String getFirstname();

    /**
     * Sets the firstname
     */
    void setFirstname(String value);

    /**
     * Provides the surname
     * @return the entity surname
     */
    String getSurname();

    /**
     * Sets the surname
     */
    void setSurname(String value);

}
