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

package de.dktk.dd.rpb.portal.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Named;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import de.dktk.dd.rpb.core.domain.Identifiable;

/**
 * TransientJsfConverter for entities implementing Identifiable interface
 *
 * @author tomas@skripcak.net
 * @since 14 Jun 2013
 */
@SuppressWarnings("unused")
@Named("transientConverter")
public class TransientJsfConverter implements Converter {

    //region Constructors

    protected TransientJsfConverter() {
        //NOOP
    }

    //endregion

    //region Methods

    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Object obj;

        try {
            int underScoreIndice = value.indexOf("_");
            String className = value.substring(0, underScoreIndice);
            String serializedObj = value.substring(underScoreIndice + 1, value.length());

            byte[] byteObj = Base64.decode(serializedObj.getBytes());
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteObj));
            obj = ois.readObject();
            ois.close();
        }
        catch (Exception e) {
            throw new ConverterException("Transient object cannot be read from stream.");
        }

        return obj;
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String retValue = null;

        if ((value != null) && (value instanceof Identifiable)) {
            Identifiable identifiable = (Identifiable) value;
            String className = identifiable.getClass().getName();

            if (identifiable.getId() == null) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutput output = new ObjectOutputStream(bos);
                    output.writeObject(identifiable);
                    byte[] byteArr = bos.toByteArray();
                    String stream = Base64.encode(byteArr);
                    retValue = identifiable.getClass().getName() + "_" + stream;
                }
                catch (IOException e) {
                    throw new ConverterException("Transient object cannot be output to the stream: " + identifiable.getClass() + ".");
                }
            }
            return retValue;
        }
        throw new ConverterException("Object is null or not implemented Identifiable interface.");
    }

    //endregion

}
