/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2018 Tomas Skripcak
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

package de.dktk.dd.rpb.portal.web.mb;

import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.repository.admin.IDefaultAccountRepository;
import de.dktk.dd.rpb.core.service.EmailService;
import org.springframework.context.annotation.Scope;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

import static de.dktk.dd.rpb.core.context.UserContext.ANONYMOUS_USER;

/**
 * Bean for contact view
 *
 * @author tomas@skripcak.net
 * @since 01 Oct 2013
 */
@Named("mbContact")
@Scope(value="view")
public class ContactBean implements Serializable {

    //region Finals

    // Different version of the class - serialisation/de-serialisation issue
    private static final long serialVersionUID = 1L;

    //endregion

    //region Members

    private EmailService emailService;

    private String to;
    private String from;
    private String subject;
    private String body;

    //endregion

    //region Constructors

    @Inject
    public ContactBean(EmailService emailService, IDefaultAccountRepository repository) {
        this.emailService = emailService;
        String userName = UserContext.getUsername();

        setFromPropertyIfUserIsLoggedIn(repository, userName);
    }

    private void setFromPropertyIfUserIsLoggedIn(IDefaultAccountRepository repository, String userName) {
        if (!userName.equals(ANONYMOUS_USER)) {
            DefaultAccount account = repository.getByUsername(userName);
            this.from = account.getEmail();
        }
    }

    //endregion

    //region Properties

    //endregion

    //region To

    public String getTo() {
        return this.to;
    }

    public void setTo(String value) {
        this.to = value;
    }

    //endregion

    //region From

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String value) {
        this.from = value;
    }

    //endregion

    //region Subject

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String value) {
        this.subject = value;
    }

    //endregion

    //region Body

    public String getBody() {
        return body;
    }

    public void setBody(String value) {
        this.body = value;
    }

    //endregion

    //endregion

    //region Commands

    /**
     * Send email to administrator
     */
    public void sendEmailToAdmin() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            this.emailService.sendMailToAdmin(this.from, this.subject, this.body);
            context.addMessage(null, new FacesMessage("Info", "E-mail successfully sent to the administrator."));
            this.clearFields();
        }
        catch (Exception err) {
            context.addMessage(null, new FacesMessage("Error", err.getMessage()));
        }
    }

    //endregion

    //region Private Methods

    private void clearFields() {
        this.from = "";
        this.to = "";
        this.subject = "";
        this.body = "";
    }

    //endregion

}
