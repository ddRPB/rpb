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

package de.dktk.dd.rpb.core.domain.ctms;

import de.dktk.dd.rpb.core.domain.Named;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * AbstractEligibilityCriterion domain entity
 *
 * @author tomas@skripcak.net
 * @since 10 Aug 2015
 */
public class AbstractEligibilityCriterion implements Named, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(AbstractEligibilityCriterion.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment
    private String name;

    // Condition (name, description, formalExpression)
    // ConditionFormalExpression (context - where to execute, value - source code for evaluation)

    //endregion

    //region Properties

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

}
