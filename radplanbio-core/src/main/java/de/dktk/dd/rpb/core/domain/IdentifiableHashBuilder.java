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

package de.dktk.dd.rpb.core.domain;

import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 * The first time the {@link #hash(Logger, Identifiable)} is called, we check if the primary key is present or not.
 * If yes: we use it to get the hash
 * If no: we use a VMID during the entire life of this instance even if later on this instance is assigned a primary key.
 */
public class IdentifiableHashBuilder implements Serializable {

    //region Members

    private static final long serialVersionUID = 1L;
    private Object technicalId;

    //endregion

    //region Methods

    public int hash(Logger log, Identifiable<?> identifiable) {

        if (technicalId == null) {
            if (identifiable.isIdSet()) {
                technicalId = identifiable.getId();
            }
            else {
                technicalId = new java.rmi.dgc.VMID();
                log.warn("DEVELOPER: hashCode is not safe." //
                        + "If you encounter this message you should take the time to carefuly " //
                        + "review the equals/hashCode methods for: " + identifiable.getClass().getCanonicalName() + " "//
                        + "You may consider using a business key.");
            }
        }

        return technicalId.hashCode();
    }

    public int hash(Logger log, Identifiable<?> identifiable, String alternateIdentifier) {

        if (technicalId == null) {
            if (alternateIdentifier != null && !alternateIdentifier.equals("")) {
                technicalId = alternateIdentifier;
            }
            else {
                technicalId = new java.rmi.dgc.VMID();
                log.warn("DEVELOPER: hashCode is not safe." //
                        + "If you encounter this message you should take the time to carefuly " //
                        + "review the equals/hashCode methods for: " + identifiable.getClass().getCanonicalName() + " "//
                        + "You may consider using a business key.");
            }
        }

        return technicalId.hashCode();
    }

    //endregion

}
