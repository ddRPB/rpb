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

package de.dktk.dd.rpb.portal.web.util;

import java.io.Serializable;

public class ColumnModel implements Serializable {

    //region Members

    private String header;
    private String property;
    private String tooltip;

    //endregion

    //region Constructors

    public ColumnModel(String header, String property, String tooltip) {
        this.header = header;
        this.property = property;
        this.tooltip = tooltip;
    }

    //endregion

    //region Properties

    public String getHeader() {
        return header;
    }

    public String getProperty() {
        return property;
    }

    public String getTooltip() {
        return tooltip;
    }

    //endregion
}
