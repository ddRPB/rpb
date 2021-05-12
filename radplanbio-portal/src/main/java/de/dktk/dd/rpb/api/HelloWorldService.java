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

package de.dktk.dd.rpb.api;

import de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.service.IConquestService;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * HelloWorld REST service that provides RPB platform health check
 */
@Path("/hello")
public class HelloWorldService extends BaseService {

    @GET
    @Path("/{param}")
    public Response getHelloMessage(@PathParam("param") String message) {

        message = message.equalsIgnoreCase("ping") ? "pong" : message;
        String output = "RPB Web-API says: " + message;
        return Response.status(200).entity(output).build();
    }

    @GET
    @Path("/status")
    public Response getStatus() {

        // RPB integration engine service user - defined in partner site hosting RPB
        DefaultAccount iEngine = this.userRepository.getByUsername(this.engineService.getUsername());

        // Connection to RPB hosting site EDC - using base URL
        IOpenClinicaService svcEdcEngine = this.createEdcConnection(iEngine, this.engineService.getPassword());
        // Connection to RPB hosting site research PACS - using base URL
        IConquestService svcPacs = this.createPacsConnection(iEngine);

        //TODO: here we implement RPB portal to components communication selfcheck

        String output = "";

        return  Response.status(200).entity(output).build();
    }

}