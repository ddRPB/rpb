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

package de.dktk.dd.rpb.participate.web.util;

import org.primefaces.component.api.UIColumn;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

/**
 * DataTable UI util methods
 *
 * @author tomas@skripcak.net
 * @since 01 Dec 2015
 */
public class DataTableUtil {

    public static List<SortMeta> buildSortOrder(String colPath, String colId, SortOrder sortOrder) {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();

        UIComponent column = viewRoot.findComponent(colPath);
        if (column != null) {
            SortMeta sm1 = new SortMeta();
            sm1.setSortBy((UIColumn) column);
            sm1.setSortField(colId);
            sm1.setSortOrder(sortOrder);

            results.add(sm1);

            return results;
        }

        return null;
    }

}
