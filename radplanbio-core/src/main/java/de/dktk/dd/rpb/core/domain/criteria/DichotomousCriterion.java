/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 Tomas Skripcak
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

package de.dktk.dd.rpb.core.domain.criteria;

import de.dktk.dd.rpb.core.domain.criteria.constraints.DichotomousConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DichotomousCriterion represents the possibility to select one from two options
 * e.g.: yes, no; male, female etc.
 *
 * @author tomas@skripcak.net
 * @since 27 Jan 2014
 *
 * Inspired by RANDI2 http://dschrimpf.github.io/randi3/
 */
@Entity
@DiscriminatorValue("DICHOTOMOUS")
public class DichotomousCriterion extends AbstractCriterion<String, DichotomousConstraint>  {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(DichotomousCriterion.class);

    //endregion

    //region Members

    private String option1;
    private String option2;

    //endregion

    //region Properties

    @Size(max = 255)
    @Column(name = "OPTION1")
    public String getOption1() {
        return this.option1;
    }

    public void setOption1(String value) {
        this.option1 = value;
    }

    @Size(max = 255)
    @Column(name = "OPTION2")
    public String getOption2() {
        return this.option2;
    }

    public void setOption2(String value) {
        this.option2 = value;
    }

    //endregion

    //region Overrides

    @Override
    @Transient
    public List<String> getConfiguredValues() {
        if (option1 == null || option2 == null || option1.isEmpty() || option2.isEmpty()) {
            return null; // The Values are not configured.
        }
        else if (configuredValues == null) {
            configuredValues = new ArrayList<String>();
            configuredValues.add(option1);
            configuredValues.add(option2);
        }
        else {
            configuredValues.clear();
            configuredValues.add(option1);
            configuredValues.add(option2);
        }

        return configuredValues;
    }

    @Override
    public void isValueCorrect(String value) throws Exception {
        if (this.option1 != null && this.option2 != null && value != null) {
            if (!Arrays.asList(this.option1, this.option2).contains(value)) {
                throw new Exception();
            }
        }
    }

    @Override
    @Transient
    public Class<DichotomousConstraint> getConstraintType() {
        return DichotomousConstraint.class;
    }

    //endregion

}
