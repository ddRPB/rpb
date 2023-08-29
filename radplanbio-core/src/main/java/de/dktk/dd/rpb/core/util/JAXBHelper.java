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

package de.dktk.dd.rpb.core.util;

import javax.xml.bind.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Helper class that provides common methods in context with JAXB
 */
public class JAXBHelper {
    /**
     * Creates an object from XML file content
     *
     * @param objectClass class of the object that will be created
     * @param file        path to the file with the XML content
     * @param <T>         Class
     * @return object of class T
     * @throws JAXBException
     */
    public static <T> T unmashalFile(Class<T> objectClass, File file) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(objectClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        JAXBElement element = (JAXBElement) unmarshaller.unmarshal(file);

        return (T) element.getValue();
    }

    public static <T> T unmashalString(Class<T> objectClass, String xmlContent) throws JAXBException {
        InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));

        JAXBElement element = unmarshalInputstream(objectClass, inputStream);

        return (T) element.getValue();
    }

    //TODO remove the *2 methods
    public static <T> T unmashalString2(Class<T> objectClass, String xmlContent) throws JAXBException {
        InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));

        T element = unmarshalInputstream2(objectClass, inputStream);

        return (T) element;
    }

    public static <T> JAXBElement unmarshalInputstream(Class<T> objectClass, InputStream inputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(objectClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        return (JAXBElement) unmarshaller.unmarshal(inputStream);
    }

    public static <T> T unmarshalInputstream2(Class<T> objectClass, InputStream inputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(objectClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (T) unmarshaller.unmarshal(inputStream);
    }

    /**
     * Marshals an object to a XML String
     *
     * @param objectClass class of the object that will br marshaled
     * @param obj         object of class T
     * @param <T>         Class
     * @return String with XMl content
     * @throws JAXBException
     */
    public static <T> String jaxbObjectToXML(Class<T> objectClass, T obj) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(objectClass);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(obj, sw);
        String xmlContent = sw.toString();

        return xmlContent;
    }
}
