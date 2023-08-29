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

package de.dktk.dd.rpb.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriUtils;

import javax.inject.Named;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.UPLOADER_URL_PATH;

@Named
public class UploaderService implements IUploaderService {
    public UploaderService() {
    }

    @Value("${rpb.uploader.protocol}")
    String uploaderProtocol;

    @Value("${rpb.uploader.host}")
    String uploaderHost;

    @Value("${rpb.uploader.port}")
    Integer uploaderPort;

    /**
     * {@inheritDoc}
     */
    public URL getUploaderUrl(List<String> parametersArray) throws UnsupportedEncodingException, MalformedURLException {

        String urlPath = UPLOADER_URL_PATH;
        String urlParameters = "";

        if (parametersArray.size() > 0) {
            urlParameters = String.join("&", parametersArray);
        }

        return new URL(this.uploaderProtocol, this.uploaderHost, this.uploaderPort, urlPath + "?" +
                UriUtils.encodePath(urlParameters, StandardCharsets.UTF_8.name()));

    }
}
