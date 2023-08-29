/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * ODM defines several Item Data Types.
 * This wrapper supports the partialDate(PDATE) type.
 */
public class PartialDateWrapper {
    private final String partialDate;
    private final String yearRegex = "^\\d{4}";
    private final String yearMonthRegex = "^\\d{4}-\\d{2}";
    private final String yearMonthDayRegex = "^\\d{4}-\\d{2}-\\d{2}";

    private final boolean isAYear;
    private final boolean isAYearAndAMonth;
    private final boolean isAYearMonthDayFormat;

    private LocalDate minDate;
    private LocalDate maxDate;

    /**
     * ODM defines several Item Data Types.
     * This wrapper supports the partialDate(PDATE) type.
     *
     * @param partialDate String that matches the patter "yyyy" or "yyyy-MM"
     * @throws MissingPropertyException thrown if the String does not match one of the expected pattern
     */
    public PartialDateWrapper(String partialDate) throws MissingPropertyException {
        verifyPartialDateParameter(partialDate);
        this.partialDate = partialDate;

        this.isAYear = partialDate.matches(yearRegex);
        this.isAYearAndAMonth = partialDate.matches(yearMonthRegex);
        this.isAYearMonthDayFormat = partialDate.matches(yearMonthDayRegex);

        verifyThatTheStringIsAPartialDate(partialDate);

        this.calculateMinMaxDate();
    }

    private void verifyThatTheStringIsAPartialDate(String partialDate) throws MissingPropertyException {
        if (!this.isAYear && !this.isAYearAndAMonth && !this.isAYearMonthDayFormat) {
            throw new MissingPropertyException("String \"" + partialDate + "\" is not a partial date");
        }
    }

    private void verifyPartialDateParameter(String partialDate) throws MissingPropertyException {
        if (partialDate == null) {
            throw new NullPointerException("partialDate String is null");
        }

        if (partialDate.isEmpty()) {
            throw new MissingPropertyException("partialDate String is empty");
        }
    }

    private void calculateMinMaxDate() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (this.isAYear) {
            String partialDateMin = this.partialDate + "-01-01";
            this.minDate = LocalDate.parse(partialDateMin, dateTimeFormatter);

            String partialDateMax = partialDate + "-12-31";
            this.maxDate = LocalDate.parse(partialDateMax, dateTimeFormatter);

        } else if (this.isAYearAndAMonth) {
            String partialDateMin = partialDate + "-01";
            this.minDate = LocalDate.parse(partialDateMin, dateTimeFormatter);

            int lastDay = this.minDate.lengthOfMonth();
            String partialDateMax = partialDate + "-" + String.valueOf(lastDay);
            this.maxDate = LocalDate.parse(partialDateMax, dateTimeFormatter);
        } else {
            // is already a date
            this.minDate = LocalDate.parse(partialDate, dateTimeFormatter);
            this.maxDate = LocalDate.parse(partialDate, dateTimeFormatter);
        }
    }

    /**
     * Partial date String matches the year pattern
     *
     * @return boolean
     */
    public boolean isAYear() {
        return isAYear;
    }

    /**
     * Partial date String matches the year-month pattern
     *
     * @return boolean
     */
    public boolean isAYearAndAMonth() {
        return isAYearAndAMonth;
    }

    /**
     * minimal date that would fit to the provided partial date String
     * examples: "2001" -> 01.01.2001, "2001-02" -> 01.02.2001
     *
     * @return LocalDate
     */
    public LocalDate getMinDate() {
        return minDate;
    }

    /**
     * maximal date that would fit to the provided partial date String
     * examples: "2001" -> 31.12.2001, "2001-02" -> 28.02.2001
     *
     * @return LocalDate
     */
    public LocalDate getMaxDate() {
        return maxDate;
    }
}
