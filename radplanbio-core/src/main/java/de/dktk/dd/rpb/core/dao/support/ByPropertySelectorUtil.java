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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Helper to create a predicate out of {@link PropertySelector}s
 *
 * @author initial source code generated by Celerio, a Jaxio product
 * @since 01 Apr 2013
 */
public class ByPropertySelectorUtil {

    //region Static

    @SuppressWarnings("unchecked")
    public static <E> Predicate byPropertySelectors(Root<E> root, CriteriaBuilder builder, final List<PropertySelector<?, ?>> selectors) {
        List<Predicate> predicates = newArrayList();

        for (PropertySelector<?, ?> selector : selectors) {
            if (selector.isBoolean()) {
                byBooleanSelector(root, builder, predicates, (PropertySelector<E, Boolean>) selector);
            } else {
                byObjectSelector(root, builder, predicates, (PropertySelector<E, ?>) selector);
            }
        }
        return JpaUtil.andPredicate(builder, predicates);
    }

    private static <E> void byBooleanSelector(Root<E> root, CriteriaBuilder builder, List<Predicate> predicates, PropertySelector<E, Boolean> selector) {
        if (selector.isNotEmpty()) {
            List<Predicate> selectorPredicates = newArrayList();

            for (Boolean selection : selector.getSelected()) {
                Path<Boolean> path = root.get(selector.getField());
                if (selection == null) {
                    selectorPredicates.add(builder.isNull(path));
                } else {
                    selectorPredicates.add(selection ? builder.isTrue(path) : builder.isFalse(path));
                }
            }
            predicates.add(JpaUtil.orPredicate(builder, selectorPredicates));
        }
    }

    private static <E> void byObjectSelector(Root<E> root, CriteriaBuilder builder, List<Predicate> predicates, PropertySelector<E, ?> selector) {
        if (selector.isNotEmpty()) {
            List<Predicate> selectorPredicates = newArrayList();

            for (Object selection : selector.getSelected()) {
                Path<?> path = root.get(selector.getField());
                selectorPredicates.add(selection == null ? builder.isNull(path) : builder.equal(path, selection));
            }
            predicates.add(JpaUtil.orPredicate(builder, selectorPredicates));
        }
    }

    //endregion

}
