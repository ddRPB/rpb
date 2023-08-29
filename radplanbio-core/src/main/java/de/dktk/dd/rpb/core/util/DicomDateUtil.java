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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility for parsing and converting DICOM dates
 *
 * @author tomas@skripcak.net
 * @since 28 Jun 2022
 */
public class DicomDateUtil {

    public static Date convertStringDateToDate(String stringDate) throws ParseException {

        Date result;

        // Check whether DICOM string date is set to something parseable
        if (stringDate != null &&
            !stringDate.isEmpty() &&
            !stringDate.equals("00000000") &&
            !stringDate.equals("Unknown")) {
            
            DateFormat format = new SimpleDateFormat(Constants.DICOM_DATEFORMAT);
            result = format.parse(stringDate);
            
        } else { // Set the RPB default DICOM date 01.01.1900
            
            Calendar defaultCalendar = Calendar.getInstance();
            defaultCalendar.set(Calendar.YEAR, 1900);
            defaultCalendar.set(Calendar.MONTH, 0);
            defaultCalendar.set(Calendar.DAY_OF_MONTH, 1);
            result = defaultCalendar.getTime();
        }

        return result;
    }
}
