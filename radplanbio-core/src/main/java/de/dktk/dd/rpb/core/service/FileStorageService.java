/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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

import org.apache.log4j.Logger;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Implementation of the interface for accessing file storage on web application server
 *
 * @author tomas@skripcak.net
 * @since 17 Feb 2016
 */
@Named
public class FileStorageService implements IFileStorageService {

    //region Finals

    private static final Logger log = Logger.getLogger(FileStorageService.class);

    //endregion

    //region Members

    private String filePath;

    //endregion

    //region Properties

    public String getFilePath() {
        return filePath;
    }

    //endregion

    //region Constructors

    public FileStorageService() {
        String resourceName = "file.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();

        try {
            InputStream resourceStream = loader.getResourceAsStream(resourceName);
            props.load(resourceStream);
            filePath = props.getProperty("storage.filePath");
        }
        catch (IOException ex) {
            log.error(ex);
        }
    }

    //endregion

    //region Methods

    public File getFile(String relativePath) {
        return new File(this.filePath + relativePath);
    }

    //endregion

}
