/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2023 RPB Team
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

import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.repository.rpb.IRadPlanBioDataRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Named("mbRpbUploader")
@Scope("request")
public class RpbUploaderBean {

    //region Injects
    private IRadPlanBioDataRepository radPlanBioDataRepository;
    // endregion

    @Inject
    public RpbUploaderBean(IRadPlanBioDataRepository radPlanBioDataRepository) {
        this.radPlanBioDataRepository = radPlanBioDataRepository;
    }

    /**
     * The RPB Uploader will request additional information that cannot be
     * delivered within the URL of the initial GET request from the browser.
     * There is a short timeout since JSF expects to render a web page.
     *
     * @throws IOException
     */
    public void renderJsonObjectForUploader() throws IOException {

        String apiKey = this.radPlanBioDataRepository.getDefaultAccountApiKeyByUsername(UserContext.getUsername());

        String st = "{" +
                " \"apiKey\" : \"" + apiKey + "\"" +
                "}";

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType(MediaType.APPLICATION_JSON_VALUE);
        externalContext.setResponseCharacterEncoding(StandardCharsets.UTF_8.name());
        externalContext.getResponseOutputWriter().write(st);
        facesContext.responseComplete();
    }

}
