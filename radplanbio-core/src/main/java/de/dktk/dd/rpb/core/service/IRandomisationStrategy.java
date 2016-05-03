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

package de.dktk.dd.rpb.core.service;

import de.dktk.dd.rpb.core.domain.randomisation.TreatmentArm;
import de.dktk.dd.rpb.core.domain.randomisation.TrialSubject;

/**
 * RandomisationStrategy interface
 *
 * @author tomas@skripcak.net
 * @since 24 Jan 2014
 */
public interface IRandomisationStrategy {

    /**
     * Randomise the study subject according to strategy
     *
     * @param subject TrialSubject
     * @return randomly chosen treatment arm
     */
    TreatmentArm randomise(TrialSubject subject);

}
