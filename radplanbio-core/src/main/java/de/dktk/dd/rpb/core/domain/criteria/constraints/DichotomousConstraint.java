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

package de.dktk.dd.rpb.core.domain.criteria.constraints;

import org.apache.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Dichotomous constraint for dichotomous criteria comparison. Dichotomous means that the option is separated to two allowed states
 * e.g.: yes, no; male, female, etc. The dichotomous constraint that represents one option selected from dichotomous criterion.
 *
 * @author tomas@skripcak.net
 * @since 27 Jan 2014
 *
 * Inspired by RANDI2 http://dschrimpf.github.io/randi3/
 */
@Entity
@DiscriminatorValue("DICHOTOMOUS")
public class DichotomousConstraint extends AbstractConstraint<String> {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(DichotomousConstraint.class);

    //endregion

    //region Members

    private String expectedValue;

    //endregion

    //region Constructors

    protected DichotomousConstraint() {
        // NOOP
    }

    public DichotomousConstraint(List<String> args) throws Exception {
        super(args);
    }

    //endregion

    //region Properties

    @Size(max = 255)
    @Column(name = "DICHOVALUE", length = 255)
    public String getExpectedValue() {
        return this.expectedValue;
    }

    public void setExpectedValue(String value) {
        this.expectedValue = value;
    }

    //endregion

    //region Overrides

    @Override
    public void isValueCorrect(String value) throws Exception {
        if (!this.expectedValue.equals(value)) {
            throw new Exception("Constraint exception.");
        }
    }

    @Override
    protected void configure(List<String> args) throws Exception {
        if (args == null || args.size() != 1) {
            throw new Exception();
        }

        this.expectedValue = args.get(0);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expectedValue == null) ? 0 : expectedValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass())   {
            return false;
        }

        DichotomousConstraint other = (DichotomousConstraint) obj;
        if (expectedValue == null) {
            if (other.expectedValue != null)  {
                return false;
            }
        }
        else if (!expectedValue.equals(other.expectedValue)) {
            return false;
        }

        return true;
    }

    //endregion

}
