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

package de.dktk.dd.rpb.api.v1.webdav;

import de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.api.support.WebDavUtils;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;

import de.dktk.dd.rpb.core.service.IOpenClinicaService;
import de.dktk.dd.rpb.core.service.OpenClinicaService;

import net.java.dev.webdav.jaxrs.methods.PROPFIND;
import net.java.dev.webdav.jaxrs.xml.elements.*;
import net.java.dev.webdav.jaxrs.xml.properties.CreationDate;
import net.java.dev.webdav.jaxrs.xml.properties.DisplayName;
import net.java.dev.webdav.jaxrs.xml.properties.GetLastModified;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import java.util.*;
import java.util.Collection;
import java.util.Date;

import static java.util.Collections.singletonList;
import static javax.ws.rs.core.Response.Status.OK;
import static net.java.dev.webdav.jaxrs.Headers.*;
import static net.java.dev.webdav.jaxrs.ResponseStatus.MULTI_STATUS;
import static net.java.dev.webdav.jaxrs.xml.properties.ResourceType.COLLECTION;

/**
 * Service handling WebDAV requests to access RPB data
 */
@Component
@Path("/v1/webdav/studies/{studyIdentifier}/sites/{siteIdentifier}/subjects/{studySubjectIdentifier}/events")
public class WebDavStudyEventService extends BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(WebDavStudyEventService.class);

    //endregion

    //region Resource Methods

    //region OPTIONS

    @OPTIONS
    public final javax.ws.rs.core.Response options() {
        return javax.ws.rs.core.Response.noContent()
                .header(DAV, "1, 2")
                .header("Allow", "PROPFIND,OPTIONS")
                .build();
    }

    //endregion

    //region PROPFIND

    @PROPFIND
    public final javax.ws.rs.core.Response propfind(
            @Context final UriInfo uriInfo,
            @PathParam("studyIdentifier") final String studyIdentifier,
            @PathParam("siteIdentifier") final String siteIdentifier,
            @PathParam("studySubjectIdentifier") final String studySubjectIdentifier,
            @DefaultValue(DEPTH_INFINITY) @HeaderParam(DEPTH) final String depth,
            @Context final Providers providers,
            @Context HttpServletRequest httpServletRequest) {

        // Workaround for windows native client that tries to lookup for things that do not exists in RPB
        if (WebDavUtils.isWindowsFileSystemIdentifier(studyIdentifier, siteIdentifier, studySubjectIdentifier)) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        // Make sure that events URL ends with slash
        String eventsUrl = uriInfo.getRequestUri().toString();
        if(!eventsUrl.endsWith("/")) {
            eventsUrl += "/";
        }
        
        // RPB integration engine service user
        DefaultAccount iEngine = this.userRepository.getByUsername(this.engineService.getUsername());
        IOpenClinicaService svcEdcEngine = this.createEdcConnection(iEngine, this.engineService.getPassword());

        final Response studyEventsFolder = new Response(
            new HRef(uriInfo.getRequestUri()),
            null,
            null,
            null,
            new PropStat(
                new Prop(
                    new DisplayName("events"),
                    new CreationDate(new Date()),
                    new GetLastModified(new Date()),
                    COLLECTION
                ),
                new Status((javax.ws.rs.core.Response.StatusType) OK)
            )
        );
        // This is the top level study events root return
        if (depth.equals(DEPTH_0)) {
            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(studyEventsFolder)).build();
        }
        // Expand
        final Collection<Response> responses = new LinkedList<>(singletonList(studyEventsFolder));

        // Init the facade with OC service with access to OC data
        this.studyIntegrationFacade.init(svcEdcEngine);
        // Load corresponding RPB study
        de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy = studyIntegrationFacade
                .loadStudyWithMetadataByIdentifier(studyIdentifier, siteIdentifier);

        if (rpbStudy != null && rpbStudy.getEdcStudy() != null) {

            // Load full ODM resource for study subject from EDC
            String trimStudySubjectIdentifier = WebDavUtils.trimStudySubjectIdentifier(studySubjectIdentifier);
            String queryOdmXmlPath = rpbStudy.getEdcStudy().getOid() + "/" + trimStudySubjectIdentifier + "/*/*";

            Odm studySubjectOdm = svcEdcEngine.getStudyCasebookOdm(
                    OpenClinicaService.CasebookFormat.XML,
                    OpenClinicaService.CasebookMethod.VIEW,
                    queryOdmXmlPath //Url
            );

            // 15 seconds - hardcoded for now
            int retryTimeout = 15000;
            long endTime = System.currentTimeMillis() + retryTimeout;
            while (studySubjectOdm == null && System.currentTimeMillis() < endTime) {
                studySubjectOdm = svcEdcEngine.getStudyCasebookOdm(
                        OpenClinicaService.CasebookFormat.XML,
                        OpenClinicaService.CasebookMethod.VIEW,
                        queryOdmXmlPath // Url
                );
            }

            // If study subject ODM exists
            if (studySubjectOdm != null) {

                // Create Definitions from References in metadata
                studySubjectOdm.updateHierarchy();
                StudySubject selectedStudySubject = studySubjectOdm.findUniqueStudySubjectOrNone(trimStudySubjectIdentifier);
                selectedStudySubject.linkOdmDefinitions(studySubjectOdm);

                // Metadata
                String dicomAnnotationType = "DICOM_STUDY_INSTANCE_UID";
                List<EventDefinition> dicomEventDefinitions = rpbStudy.findAnnotatedEventDefinitions(dicomAnnotationType);

                // Data
                List<EventData> dicomEventDataList = selectedStudySubject.getEventOccurrencesForEvenDefs(dicomEventDefinitions);

                // There are events scheduled for that subject
                for (EventData eventData : dicomEventDataList) {

                    // Clear the event name identifier from white spaces
                    String studyEventNameIdentifier = WebDavUtils.buildEventIdentifier(eventData);

                    Response eventFolder = new Response(
                            new HRef(eventsUrl + studyEventNameIdentifier),
                            null,
                            null,
                            null,
                            new PropStat(
                                    new Prop(
                                            new DisplayName(studyEventNameIdentifier),
                                            new CreationDate(eventData.getComparableStartDate()),
                                            new GetLastModified(eventData.getComparableStartDate()),
                                            COLLECTION
                                    ),
                                    new Status((javax.ws.rs.core.Response.StatusType) OK)
                            )
                    );

                    responses.add(eventFolder);
                }

                return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new Response[0]))).build();
            }
            else {
                log.warn("WebDAV: Requested study subject does not exist within requested study.");
                return javax.ws.rs.core.Response.status(404).build();
            }
        }
        else {
            log.error("WebDAV: OC SOAP list all studies failed, probably too many parallel requests.");
            return javax.ws.rs.core.Response.status(500).build();
        }
    }

    @PROPFIND
    @Path("{eventIdentifier}")
    public final javax.ws.rs.core.Response profind(
            @PathParam("studyIdentifier") final String studyIdentifier,
            @PathParam("siteIdentifier") final String siteIdentifier,
            @PathParam("studySubjectIdentifier") final String studySubjectIdentifier,
            @PathParam("eventIdentifier") final String eventIdentifier,
            @Context final UriInfo uriInfo,
            @Context final Providers providers,
            @Context HttpServletRequest httpServletRequest) {
        
        // Workaround for windows native client that tries to lookup for things that do not exists in RPB
        if (WebDavUtils.isWindowsFileSystemIdentifier(studyIdentifier, siteIdentifier, studySubjectIdentifier)) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        // Make sure that event URL ends with slash
        String eventUrl = uriInfo.getRequestUri().toString();
        if (!eventUrl.endsWith("/")) {
            eventUrl += "/";
        }

        if (studyIdentifier != null && !studyIdentifier.isEmpty() &&
            siteIdentifier != null && !siteIdentifier.isEmpty() &&
            studySubjectIdentifier != null && !studySubjectIdentifier.isEmpty() &&
            eventIdentifier != null && !eventIdentifier.isEmpty()) {

            final Response eventFolder  = new Response(
                new HRef(eventUrl),
                null,
                null,
                null,
                new PropStat(
                    new Prop(
                        new DisplayName(eventIdentifier),
                        new CreationDate(new Date()),
                        new GetLastModified(new Date()),
                        COLLECTION
                    ),
                    new Status((javax.ws.rs.core.Response.StatusType) OK)
                )
            );
            
            final Collection<Response> responses = new LinkedList<>(singletonList(eventFolder));
            Response dicomStudiesFolder = new Response(
                new HRef(eventUrl + "dcmstudies"),
                null,
                null,
                null,
                new PropStat(
                    new Prop(
                        new DisplayName("dcmstudies"),
                        new CreationDate(new Date()),
                        new GetLastModified(new Date()),
                        COLLECTION
                    ),
                    new Status((javax.ws.rs.core.Response.StatusType) OK)
                )
            );
            
            responses.add(dicomStudiesFolder);

            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new Response[0]))).build();
        }
        else {
            log.error("WebDAV: Requested study/site/subject/event identifier is null or empty.");
            return javax.ws.rs.core.Response.status(500).build();
        }
    }

    //endregion

    //endregion

}
