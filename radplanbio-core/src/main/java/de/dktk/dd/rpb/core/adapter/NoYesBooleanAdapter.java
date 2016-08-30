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

package de.dktk.dd.rpb.core.adapter;

import de.dktk.dd.rpb.core.util.Constants;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter for mapping No, Yes values from CDISC ODM to java boolean
 *
 * @author tomas@skripcak.net
 * @since 04 Jul 2016
 */
public class NoYesBooleanAdapter extends XmlAdapter<String, Boolean> {

    //region Overrides

    @Override
    public Boolean unmarshal(String s) {
        return s == null ? null : s.equals(Constants.OC_YES);
    }

    @Override
    public String marshal(Boolean b) {
        return b == null ? null : b ? Constants.OC_YES : Constants.OC_NO;
    }

    //endregion

}