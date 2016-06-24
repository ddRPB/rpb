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

package de.dktk.dd.rpb.portal.web.mb.pacs;

import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Named;

/**
 * ViewModel bean for PACS (User PartnerSite PACS) centric DICOM lookup
 *
 * @author tomas@skripcak.net
 * @since 22 September 2015
 */
@Named("mbDicomLookup")
@Scope("view")
public class DicomLookupBean {



    //region Members

    private String viewType;

    //endregion

    //region Properties

    public String getViewType() {
        return this.viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.viewType = "STUDY";

//        this.setColumnVisibilityList(
//                this.buildColumnVisibilityList()
//        );
//        this.setPreSortOrder(
//                this.buildSortOrder()
//        );
//
//        this.load();
    }

    //endregion

    //region Commands

    public void search() {

    }

    //endregion

}
