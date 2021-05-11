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

package de.dktk.dd.rpb.core.util;

/**
 * MathUtil implements methods for common math functions
 */
public final class MathUtil {

    /**
     * Calculate recursively greatest common divisor of two integer numbers
     * @param a number
     * @param b number
     * @return greatest common divisor
     */
    public static int gcd(int a, int b) {
        if(a == 0) {
            return b;
        }
        if(b == 0) {
            return a;
        }

        if(a > b) {
            return gcd(b, a % b);
        }

        return gcd(a, b % a);
    }

}
