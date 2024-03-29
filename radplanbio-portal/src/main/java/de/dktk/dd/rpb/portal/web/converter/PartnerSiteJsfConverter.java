/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2014  Tomas Skripcak
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

package de.dktk.dd.rpb.portal.web.converter;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.repository.admin.IPartnerSiteRepository;
import org.springframework.core.convert.support.DefaultConversionService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * JSF Converter for {@link PartnerSite}.
 */
@Named("partnerSiteConverter")
public class PartnerSiteJsfConverter implements Converter, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Injects

    @Inject
    private IPartnerSiteRepository IPartnerSiteRepository;

    public IPartnerSiteRepository getIPartnerSiteRepository() {
        return this.IPartnerSiteRepository;
    }

    //endregion

    //region Members

    private Class<Integer> pkType;

    //endregion

    //region Protected

    protected DefaultConversionService conversionService = new DefaultConversionService();

    //endregion

    //region Constructors

    public PartnerSiteJsfConverter() {
        this.pkType = Integer.class;
    }

    //endregion

    //region Overrides

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || "-1".equals(value)) {
            return null;
        }

        return this.getIPartnerSiteRepository().getById(toPk(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String result = "-1";

        if (value == null || !Identifiable.class.isAssignableFrom(value.getClass())) {
            return result;
        }

        if (((Identifiable<Integer>) value).getId() != null) {
            result = ((Identifiable<Integer>) value).getId().toString();
        }

        return result;
    }

    //endregion

    //region Methods

    protected int toPk(String value) {
        return conversionService.convert(value, pkType);
    }

    //endregion

}