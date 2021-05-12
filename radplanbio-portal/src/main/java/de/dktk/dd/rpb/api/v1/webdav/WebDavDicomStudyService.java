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

package de.dktk.dd.rpb.api.v1.webdav;

import  de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.api.support.WebDavUtils;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.service.OpenClinicaService;
import de.dktk.dd.rpb.core.service.*;
import de.dktk.dd.rpb.core.util.Constants;

import net.java.dev.webdav.jaxrs.methods.PROPFIND;
import net.java.dev.webdav.jaxrs.xml.elements.*;
import net.java.dev.webdav.jaxrs.xml.elements.Status;
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

import static java.util.Collections.singletonList;
import static javax.ws.rs.core.Response.Status.OK;
import static net.java.dev.webdav.jaxrs.Headers.*;
import static net.java.dev.webdav.jaxrs.ResponseStatus.MULTI_STATUS;
import static net.java.dev.webdav.jaxrs.xml.properties.ResourceType.COLLECTION;

/**
 * Service handling WebDAV requests to access RPB data
 */
@Component
@Path("/v1/webdav/studies/{studyIdentifier}/sites/{siteIdentifier}/subjects/{studySubjectIdentifier}/events/{eventIdentifier}/dcmstudies")
public class WebDavDicomStudyService  extends BaseService{

    //region Finals

    private static final Logger log = Logger.getLogger(WebDavDicomStudyService.class);

    //endregion

    //region Resource Methods

    //region OPTION

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
            @PathParam("eventIdentifier") final String eventIdentifier,
            @DefaultValue(DEPTH_INFINITY) @HeaderParam(DEPTH) final String depth,
            @Context final Providers providers,
            @Context HttpServletRequest httpServletRequest) {

        // Workaround for windows native client that tries to lookup for things that do not exists in RPB
        if (WebDavUtils.isWindowsFileSystemIdentifier(studyIdentifier, siteIdentifier, studySubjectIdentifier, eventIdentifier)) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        // Authenticate user
        DefaultAccount defaultAccount = this.defaultAccountAuthentication(httpServletRequest);
        IConquestService svcPacs = this.createPacsConnection(defaultAccount);

        // RPB integration engine service user
        DefaultAccount iEngine = this.userRepository.getByUsername(this.engineService.getUsername());
        IOpenClinicaService svcEdcEngine = this.createEdcConnection(iEngine, this.engineService.getPassword());

        // Make sure that Dicom studies URL ends with slash
        String dicomStudiesUrl = uriInfo.getRequestUri().toString();
        if(!dicomStudiesUrl.endsWith("/")) {
            dicomStudiesUrl += "/";
        }
        final Response dicomStudiesFolder  = new Response(
            new HRef(dicomStudiesUrl),
            null,
            null,
            null,
            new PropStat(
                new Prop(
                    new DisplayName("dcmstudies"),
                    new CreationDate(new Date()),
                    new GetLastModified(new Date()), COLLECTION
                ),
                new Status((javax.ws.rs.core.Response.StatusType) OK)
            )
        );
        // This is the top level study events root return
        if (depth.equals(DEPTH_0)) {
            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(dicomStudiesFolder)).build();
        }

        final Collection<Response> responses = new LinkedList<>(singletonList(dicomStudiesFolder));

        // Init the facade with OC service with access to OC data
        this.studyIntegrationFacade.init(svcEdcEngine);
        // Load corresponding RPB study
        de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy = studyIntegrationFacade
                .loadStudyWithMetadataByIdentifier(studyIdentifier, siteIdentifier);

        if (rpbStudy != null && rpbStudy.getEdcStudy() != null) {

            // Load full ODM resource for study subject from EDC
            String trimStudySubjectIdentifier = WebDavUtils.trimStudySubjectIdentifier(studySubjectIdentifier);
            String queryOdmXmlPath = rpbStudy.getEdcStudy().getOid() + "/" + trimStudySubjectIdentifier + "/*/*";

            // With too many parallel requests this can fail
            Odm studySubjectOdm = svcEdcEngine.getStudyCasebookOdm(
                    OpenClinicaService.CasebookFormat.XML,
                    OpenClinicaService.CasebookMethod.VIEW,
                    queryOdmXmlPath // Url
            );

            // 15 seconds - hardcoded for now
            int retryTimeout = 15000;
            long endTime = System.currentTimeMillis() + retryTimeout;
            while (studySubjectOdm == null && System.currentTimeMillis() < endTime) {
                this.sleepSecond();
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

                // Load DICOM data for patient from PACS (all at once, later it will be filtered)
                List<DicomStudy> allPatientDicomStudies = svcPacs.loadPatientStudies(selectedStudySubject.getPid());

                // Lookup for selected study event
                for (EventData eventData : dicomEventDataList) {
                    if (WebDavUtils.buildEventIdentifier(eventData).equals(eventIdentifier)) {

                        // Just annotation concerning selected event
                        CrfFieldAnnotation eventExample = new CrfFieldAnnotation();
                        eventExample.setEventDefinitionOid(eventData.getStudyEventOid());

                        // Load CRF items referencing DICOM studies
                        List<ItemData> dicomCrfItemData = eventData.findAnnotatedItemData(
                                rpbStudy.findAnnotations(dicomAnnotationType, eventExample)
                        );

                        // Filter only DICOM studies that care referenced in item data
                        List<DicomStudy> dicomStudies = new ArrayList<>();
                        for (ItemData id : dicomCrfItemData) {
                            if (!"".equals(id.getValue())) {
                                for (DicomStudy ds : allPatientDicomStudies) {
                                    if (ds.getStudyInstanceUID().equals(id.getValue())) {
                                        dicomStudies.add(ds);
                                        break;
                                    }
                                }
                            }
                        }

                        if (dicomStudies.size() > 0) {
                            for (DicomStudy dicomStudy : dicomStudies) {

                                String dicomStudyNameIdentifier = dicomStudy.getStudyType() +
                                        Constants.RPB_IDENTIFIERSEP +
                                        WebDavUtils.encodeDicomUid(dicomStudy.getStudyInstanceUID());

                                Response studyDicomFolder = new Response(
                                    new HRef(dicomStudiesUrl + dicomStudyNameIdentifier),
                                    null,
                                    null,
                                    null,
                                    new PropStat(
                                        new Prop(
                                            new DisplayName(dicomStudyNameIdentifier),
                                            new CreationDate(dicomStudy.getDateStudy()),
                                            new GetLastModified(dicomStudy.getDateStudy()),
                                            COLLECTION
                                        ),
                                        new Status((javax.ws.rs.core.Response.StatusType) OK)
                                    )
                                );

                                responses.add(studyDicomFolder);
                            }
                        }
                    }
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
    @Path("{dicomStudyIdentifier}")
    public final javax.ws.rs.core.Response profind(
            @PathParam("studyIdentifier") final String studyIdentifier,
            @PathParam("siteIdentifier") final String siteIdentifier,
            @PathParam("studySubjectIdentifier") final String studySubjectIdentifier,
            @PathParam("eventIdentifier") final String eventIdentifier,
            @PathParam("dicomStudyIdentifier") final String dicomStudyIdentifier,
            @Context final UriInfo uriInfo,
            @Context final Providers providers,
            @Context HttpServletRequest httpServletRequest) {
        
        // Workaround for windows native client that tries to lookup for things that do not exists in RPB
        if (WebDavUtils.isWindowsFileSystemIdentifier(studyIdentifier, siteIdentifier, studySubjectIdentifier, eventIdentifier, dicomStudyIdentifier)) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        // Make sure that selected dicom studies URL ends with slash
        String dicomStudyUrl = uriInfo.getRequestUri().toString();
        if (!dicomStudyUrl.endsWith("/")) {
            dicomStudyUrl += "/";
        }

        if (studyIdentifier != null && !studyIdentifier.isEmpty() &&
            siteIdentifier != null && !siteIdentifier.isEmpty() &&
            studySubjectIdentifier != null && !studySubjectIdentifier.isEmpty() &&
            eventIdentifier != null && !eventIdentifier.isEmpty() &&
            dicomStudyIdentifier != null && !dicomStudyIdentifier.isEmpty()) {

            final Response dicomStudyFolder = new Response(
                new HRef(dicomStudyUrl),
                null,
                null,
                null,
                new PropStat(
                    new Prop(
                        new DisplayName(dicomStudyIdentifier),
                        new CreationDate(new Date()),
                        new GetLastModified(new Date()),
                        COLLECTION
                    ),
                    new Status((javax.ws.rs.core.Response.StatusType) OK)
                )
            );

            final Collection<Response> responses = new LinkedList<>(singletonList(dicomStudyFolder));
            Response dicomSeriesFolder = new Response(
                new HRef(dicomStudyUrl + "dcmseries"),
                null,
                null,
                null,
                new PropStat(
                    new Prop(
                        new DisplayName("dcmseries"),
                        new CreationDate(new Date()),
                        new GetLastModified(new Date()),
                        COLLECTION
                    ),
                    new Status((javax.ws.rs.core.Response.StatusType) OK)
                )
            );
            
            responses.add(dicomSeriesFolder);

            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new Response[0]))).build();
        }
        else {
            log.error("WebDAV: Requested study/site/subject/event/dcmstudy identifier is null or empty.");
            return javax.ws.rs.core.Response.status(500).build();
        }
    }
    
    //endregion

    //endregion
    
}