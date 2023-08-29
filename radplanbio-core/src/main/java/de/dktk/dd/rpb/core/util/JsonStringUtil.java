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

public class JsonStringUtil {
    /**
     * Get rid of carriage returns and new rows that will mess up string to JSON conversion
     *
     * @param untrimmedString Probably not JSON convertible String
     * @return JSON convertible String
     */
    public static String trimJsonString(String untrimmedString) {
        return untrimmedString.replaceAll("(\\r|\\n)", "");
    }
}
