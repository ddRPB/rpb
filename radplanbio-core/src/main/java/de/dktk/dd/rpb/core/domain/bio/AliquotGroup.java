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

package de.dktk.dd.rpb.core.domain.bio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * AliquotGroup transient domain entity
 *
 * @author tomas@skripcak.net
 * @since 04 Jan 2017
 */
public class AliquotGroup extends AbstractSpecimen {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(AliquotGroup.class);

    //endregion

    //region Members

    private int initialAliquots;
    private int currentAliquots;

    //endregion

    //region Constructors

    public AliquotGroup() {
        this.children = new ArrayList<>();
    }

    //endregion

    //region Properties

    //region Initial Aliquots

    public int getInitialAliquots() {
        return initialAliquots;
    }

    public void setInitialAliquots(int initialAliquots) {
        this.initialAliquots = initialAliquots;
    }

    //endregion

    //region Current Aliquots

    public int getCurrentAliquots() {
        return currentAliquots;
    }

    public void setCurrentAliquots(int currentAliquots) {
        this.currentAliquots = currentAliquots;
    }

    //endregion

    //endregion

}
