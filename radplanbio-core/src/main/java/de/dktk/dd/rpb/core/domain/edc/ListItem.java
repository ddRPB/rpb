/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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

package de.dktk.dd.rpb.core.domain.edc;

import de.dktk.dd.rpb.core.domain.Identifiable;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * ListItem CDISC ODM domain entity
 *
 * @author tomas@skripcak.net
 * @since 18 Mar 2015
 */
public abstract class ListItem implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ListItem.class);

    //ednregion

    //region Properties

    public abstract String getCodedValue();

    public abstract void setCodedValue(String value);

    public abstract String getDecodedText();

    public abstract void setDecodedText(String value);

    //endregion

}