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

package de.dktk.dd.rpb.api.v1;

import de.dktk.dd.rpb.api.support.BaseService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.ws.rs.Path;

/**
 * Service handling Patients as aggregate root resources (patient centric)
 */
@Component
@Path("/v1/patients")
public class PatientService extends BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(PatientService.class);

    //endregion

    // Patient resource need to use pseudonym as identifier


}
