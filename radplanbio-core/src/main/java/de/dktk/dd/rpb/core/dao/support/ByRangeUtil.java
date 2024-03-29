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

package de.dktk.dd.rpb.core.dao.support;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Helper to create a predicate out of {@link Range}s.
 *
 * @author initial source code generated by Celerio, a Jaxio product
 * @since 01 Apr 2013
 */
public class ByRangeUtil {

    //region Static

    public static <E> Predicate byRanges(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder, final List<Range<?, ?>> ranges, final Class<E> type) {

        List<Predicate> predicates = newArrayList();
        for (Range<?, ?> r : ranges) {
            @SuppressWarnings("unchecked")
            Range<E, ? extends Comparable<? super Comparable>> range = (Range<E, ? extends Comparable<? super Comparable>>) r;
            if (range.isSet()) {
                Predicate rangePredicate = buildRangePredicate(range, root, builder);

                if (rangePredicate != null) {
                    if (!range.isIncludeNullSet() || range.getIncludeNull() == FALSE) {
                        predicates.add(rangePredicate);
                    } else {
                        predicates.add(builder.or(rangePredicate, builder.isNull(root.get(range.getField()))));
                    }
                } else {
                    // no from/to is set, but include null or not could be:
                    if (TRUE == range.getIncludeNull()) {
                        predicates.add(builder.isNull(root.get(range.getField())));
                    } else if (FALSE == range.getIncludeNull()) {
                        predicates.add(builder.isNotNull(root.get(range.getField())));
                    }
                }
            }
        }

        return JpaUtil.andPredicate(builder, predicates);
    }

    private static <D extends Comparable<? super D>, E> Predicate buildRangePredicate(Range<E, D> range, Root<E> root, CriteriaBuilder builder) {
        if (range.isBetween()) {
            return builder.between(root.get(range.getField()), range.getFrom(), range.getTo());
        } else if (range.isFromSet()) {
            return builder.greaterThanOrEqualTo(root.get(range.getField()), range.getFrom());
        } else if (range.isToSet()) {
            return builder.lessThanOrEqualTo(root.get(range.getField()), range.getTo());
        }
        return null;
    }

    //endregion

}