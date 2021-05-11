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

package de.dktk.dd.rpb.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Email service
 *
 * @author tomas@skripcak.net
 * @since 22 Jan 2014
 */
@Named
public class EmailService {

    //region Injects

    @Inject
    private MailSender mailSender;

    @Value("${email.admin:}")
    private String emailAdmin;

    //endregion

    //region Methods

    @SuppressWarnings("unused")
    public void sendMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        this.mailSender.send(message);
    }

    public void sendMailToAdmin(String from, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(this.emailAdmin);
        message.setSubject(subject);
        message.setText(body);

        this.mailSender.send(message);
    }

    //endregion

}