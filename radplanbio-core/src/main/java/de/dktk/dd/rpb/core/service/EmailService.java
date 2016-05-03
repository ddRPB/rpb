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

package de.dktk.dd.rpb.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Email service
 *
 * @author tomas@skripcak.net
 * @since 22 Januar 2014
 */
@Service("emailService")
public class EmailService {

    //region Injects

    @Autowired
    private MailSender mailSender;

    //endregion

    //region Methods

    public void sendMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        this.mailSender.send(message);
    }

    public void sendMailToAdmin(String from, String subject, String body) {
        String emailAdmin = "";

        String resourceName = "email.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();

        try {
            InputStream resourceStream = loader.getResourceAsStream(resourceName);
            props.load(resourceStream);
            emailAdmin= props.getProperty("adminEmail");

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(emailAdmin);
        message.setSubject(subject);
        message.setText(body);

        this.mailSender.send(message);
    }

    //endregion

}