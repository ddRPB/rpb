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

package de.dktk.dd.rpb.core.service;

import de.dktk.dd.rpb.core.domain.edc.*;

import javax.inject.Named;

/**
 * OdmService
 *
 * @author tomas@skripcak.net
 * @since 07 Jun 2016
 */
@Named
public class OdmService implements IOdmService {

    public OdmMatch compareOdm(Odm sourceOdm, Odm targetOdm) {

        // Init the OdmMatch structure (OIDs) before the comparison starts
        return new OdmMatch(sourceOdm, targetOdm);
    }

}
