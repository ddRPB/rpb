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

package de.dktk.dd.rpb.portal.web.controllers.edc;

import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.repository.admin.DefaultAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * EDC controller
 *
 * EDC controller is Spring controller class which is responsible for communication with EDC system directly
 * via accessing the URL
 *
 * @author tomas@skripcak.net
 * @since 12 May 2013
 */
@Controller
public class EdcController {

    //region Injects

    @Autowired
    public void setDefaultAccountRepository(DefaultAccountRepository value) {
        this.defaultAccountRepository = value;
    }

    //endregion

    //region Members

    DefaultAccountRepository defaultAccountRepository;

    //endregion

    //region User Default Account

    public DefaultAccount getMyAccount() {
        return this.defaultAccountRepository.getByUsername(UserContext.getUsername());
    }

    //endregion

    //region Methods

    /**
     * Send a http request to open Electronic data caputre tool according to address in database
     *
     * @param request - HttpServletRequest - for checking query string parameters
     * @param response - HttpServletResponse - for creating of HTML view
     */
    @RequestMapping(value="/edc/openElectronicDataCapture.faces", method = RequestMethod.GET)
    @SuppressWarnings("unused")
    public void showDicomViewer(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect(this.getMyAccount().getPartnerSite().getEdc().getEdcBaseUrl());
        }
        catch (Exception err) {
            // Log error
        }
    }

    //endregion

}
