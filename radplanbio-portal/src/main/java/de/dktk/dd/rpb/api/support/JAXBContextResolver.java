/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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

package de.dktk.dd.rpb.api.support;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Register WebDAV classes to be recognised by JAXBContext
 */
@Provider
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class JAXBContextResolver implements ContextResolver<JAXBContext> {

    private JAXBContext context;
    private Class[] types = {
        net.java.dev.webdav.jaxrs.xml.elements.MultiStatus.class,
        net.java.dev.webdav.jaxrs.xml.elements.Prop.class,
        net.java.dev.webdav.jaxrs.xml.properties.DisplayName.class,
        net.java.dev.webdav.jaxrs.xml.properties.CreationDate.class,
        net.java.dev.webdav.jaxrs.xml.properties.GetLastModified.class,
        net.java.dev.webdav.jaxrs.xml.properties.ResourceType.class,
        net.java.dev.webdav.jaxrs.xml.elements.Collection.class,
        net.java.dev.webdav.jaxrs.xml.properties.GetContentLength.class,
        net.java.dev.webdav.jaxrs.xml.properties.GetContentType.class
    };

    public JAXBContextResolver() {
        try {
            this.context = JAXBContext.newInstance(types);
        }
        catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }

    public JAXBContext getContext(Class<?> objectType) {
        for (Class type : types) {
            if (type == objectType) {
                return context;
            }
        }
        
        return null;
    }
    
}