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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import java.util.concurrent.Callable;

/**
 * Jersey client that allows to be run in parallel
 *
 * @since 07 May 2021
 */
public class CallableJerseyClient implements Callable<String> {
    private final String url;
    private final String userName;
    private final String password;

    /**
     * Constructor for CallableJerseyClient
     *
     * @param url URl for the GET request
     * @param userName user name will be used for authentication if provided
     * @param password password will be used for authentication if provided
     */
    public CallableJerseyClient(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    /**
     * Call method that will perform the GET request
     *
     * @return String JSON String with the result of the GET request
     * @throws Exception If the result status != 200 the exception will be thrown
     */
    @Override
    public String call() throws Exception {
        Client client = Client.create();

        addAuthenticationFilterIfUsernameIsProvided(client);

        WebResource webResource = client.resource(url);
        ClientResponse response = webResource.get(ClientResponse.class);

        throwIfResponseStatusIsNotTwoHundred(response);

        String resultString = "";

        if (response.hasEntity()) {
            resultString = response.getEntity(String.class);
        }

        return resultString;

    }

    private void addAuthenticationFilterIfUsernameIsProvided(Client client) {
        if (userName != null && !userName.isEmpty()) {
            client.addFilter(new HTTPBasicAuthFilter(userName, password));
        }
    }

    private void throwIfResponseStatusIsNotTwoHundred(ClientResponse response) throws Exception {
        if (response.getStatus() != 200) {
            String errorMessage = "There was a problem with your request. ";
            throw new Exception(errorMessage + " URL: " + url +
                    " Status code: " + response.getStatus() + " " + response.getStatusInfo().toString());
        }
    }

}
