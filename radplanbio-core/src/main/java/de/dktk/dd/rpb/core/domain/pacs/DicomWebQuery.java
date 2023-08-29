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

package de.dktk.dd.rpb.core.domain.pacs;

import org.dcm4che3.data.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 4/21/17.
 */
public class DicomWebQuery {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(DicomWebQuery.class);

    //endregion

    //region Members

    private Map<Tag, DicomWebQueryAttribute> queryAttributes;
    private List<Tag> includeFields;
    private Boolean includeAll;
    private Boolean fuzzyMatching;
    private Integer limit;
    private Integer offset;
    private List<String> errors;

    //endregion

    //region Constructors

    public DicomWebQuery() {
        this.queryAttributes = new HashMap<>();
        this.includeFields = new ArrayList<>();
        this.includeAll = Boolean.FALSE;
        this.fuzzyMatching = Boolean.FALSE;
        this.limit = 50;
        this.offset = 0;
        this.errors = new ArrayList<>();
    }

    //endregion

    //region Properties

    public Map<Tag, DicomWebQueryAttribute> getQueryAttributes() {
        return queryAttributes;
    }

    public void setQueryAttributes(Map<Tag, DicomWebQueryAttribute> queryAttributes) {
        this.queryAttributes = queryAttributes;
    }

    public List<Tag> getIncludeFields() {
        return includeFields;
    }

    public void setIncludeFields(List<Tag> includeFields) {
        this.includeFields = includeFields;
    }

    public Boolean getIncludeAll() {
        return includeAll;
    }

    public void setIncludeAll(Boolean includeAll) {
        this.includeAll = includeAll;
    }

    public Boolean getFuzzyMatching() {
        return fuzzyMatching;
    }

    public void setFuzzyMatching(Boolean fuzzyMatching) {
        this.fuzzyMatching = fuzzyMatching;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    //endregion

    //region Methods

    public DicomWebQueryAttribute findQueryAttribute(Tag tag) {
        return this.queryAttributes.get(tag);
    }

    //endregion

}
