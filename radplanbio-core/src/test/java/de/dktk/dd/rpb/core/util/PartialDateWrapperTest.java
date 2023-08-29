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

package de.dktk.dd.rpb.core.util;

import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class PartialDateWrapperTest {

    // region constructor

    @Test(expected = NullPointerException.class)
    public void throws_null_pointer_exception_if_parameter_is_null() throws MissingPropertyException {
        new PartialDateWrapper(null);
    }

    @Test(expected = MissingPropertyException.class)
    public void throws_missing_property_exception_if_parameter_is_null() throws MissingPropertyException {
        new PartialDateWrapper("");
    }

    // endregion

    // region isAYear

    @Test(expected = MissingPropertyException.class)
    public void character_is_not_a_year() throws MissingPropertyException {
        PartialDateWrapper partialDateWrapper = new PartialDateWrapper("-1234");
    }

    @Test
    public void year_and_month_is_not_just_a_year() throws MissingPropertyException {
        PartialDateWrapper partialDateWrapper = new PartialDateWrapper("2004-12");
        assertFalse(partialDateWrapper.isAYear());
    }

    @Test
    public void is_a_year() throws MissingPropertyException {
        PartialDateWrapper partialDateWrapper = new PartialDateWrapper("1987");
        assertTrue(partialDateWrapper.isAYear());
    }

    // endregion

    // region isAYearAndAMonth

    @Test(expected = MissingPropertyException.class)
    public void character_is_not_a_year_month() throws MissingPropertyException {
        PartialDateWrapper partialDateWrapper = new PartialDateWrapper("-1234-02");
    }

    @Test
    public void a_year_is_not_a_year_month() throws MissingPropertyException {
        PartialDateWrapper partialDateWrapper = new PartialDateWrapper("1987");
        partialDateWrapper.getMinDate();
        assertFalse(partialDateWrapper.isAYearAndAMonth());
    }

    @Test
    public void is_a_year_and_month() throws MissingPropertyException {
        PartialDateWrapper partialDateWrapper = new PartialDateWrapper("2004-12");
        partialDateWrapper.getMinDate();
        assertTrue(partialDateWrapper.isAYearAndAMonth());
    }

    // endregion

    // region min and max date

    @Test
    public void min_and_max_from_year() throws MissingPropertyException {
        PartialDateWrapper partialDateWrapper = new PartialDateWrapper("1987");
        LocalDate expectedMinDate = LocalDate.of(1987,01,01);
        LocalDate expectedMaxDate = LocalDate.of(1987,12,31);
        assertEquals(expectedMinDate, partialDateWrapper.getMinDate());
        assertEquals(expectedMaxDate, partialDateWrapper.getMaxDate());
    }

    @Test
    public void min_and_max_from_year_and_month() throws MissingPropertyException {
        PartialDateWrapper partialDateWrapper = new PartialDateWrapper("2020-02");
        LocalDate expectedMinDate = LocalDate.of(2020,02,01);
        LocalDate expectedMaxDate = LocalDate.of(2020,02,29);
        assertEquals(expectedMinDate, partialDateWrapper.getMinDate());
        assertEquals(expectedMaxDate, partialDateWrapper.getMaxDate());
    }

    @Test
    public void min_and_max_from_year_month_date() throws MissingPropertyException {
        PartialDateWrapper partialDateWrapper = new PartialDateWrapper("1987-01-01");
        LocalDate expectedMinDate = LocalDate.of(1987,01,01);
        LocalDate expectedMaxDate = LocalDate.of(1987,01,01);
        assertEquals(expectedMinDate, partialDateWrapper.getMinDate());
        assertEquals(expectedMaxDate, partialDateWrapper.getMaxDate());
    }

    // endregion
}