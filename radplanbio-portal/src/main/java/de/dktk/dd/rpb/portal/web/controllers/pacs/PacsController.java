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

package de.dktk.dd.rpb.portal.web.controllers.pacs;

import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.repository.admin.DefaultAccountRepository;
import de.dktk.dd.rpb.portal.web.listeners.HttpSessionCollector;
import de.dktk.dd.rpb.portal.web.util.BrowserDetection;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;

import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;

/**
 * PACS controller
 *
 * PACS controller is Spring controller class which is responsible for communication with ConQuest web server
 * It obtains the HTTP request coming from OpenClinica custo CRF with parameters encoded in query string
 * Afterward is proxy the request to not publicly available ConQuest web server
 * ConQuest web server produces HTML and PACS controller take this HTML and put it as a response
 *
 * @author tomas@skripcak.net
 * @since 12 May 2013
 */
@Controller
public class PacsController {

    //region Injects

    @Autowired
    public void setDefaultAccountRepository(DefaultAccountRepository value) {
        this.defaultAccountRepository = value;
    }

    //endregion

    //region Members

    private DefaultAccountRepository defaultAccountRepository;

    //endregion

    //region Study Site

    public PartnerSite getStudySite() {
        // User Account
        DefaultAccount rpbAccount = this.defaultAccountRepository.getByUsername(UserContext.getUsername());
        // Return the user account partner site
        return rpbAccount.getPartnerSite();
    }

    //endregion

    //region Methods

    //region DWV

    /**
     * Send a http request to ConQuest web server in order to invoke the HTML DICOM Viewer
     *
     * @param request - HttpServletRequest - for checking query string parameters
     * @param response - HttpServletResponse - for creating of HTML view
     */
    @RequestMapping(value="/pacs/studySubjectView.faces", method = RequestMethod.GET)
    public void showDicomViewer(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            String cgiUrl = this.getStudySite().getPacs().getPacsBaseUrl();
            String mode = "";

            // These are the identifications according to which we are going to browse the PACS server
            String patientIdMatch = "";
            String seriesUID = "";
            String queryStrings = "";

            // Public URL for proxy (this URL is used in lua code in Conquest in order to generate javascript)
            String proxy = this.getStudySite().getPortal().getPublicUrl();

            // Look in the query string to find which mode to use for ConQuest
             if (request.getParameter("mode") != null) {
                mode = request.getParameter("mode");
            }

            // Look for query strings which are necessary for DICOM viewer
            if (mode.equals("radplanbioview") || mode.equals("radplanbiosimpleview")) {
                if (request.getParameter("patientId") != null) {
                    patientIdMatch = request.getParameter("patientId");
                }
                if (request.getParameter("seriesUID") != null) {
                    seriesUID =  request.getParameter("seriesUID");
                }

                if (!this.viewerHasSupportForBrowser(request)) {
                    mode = "radplanbioviewold";
                }

                queryStrings = "?mode=" + mode +
                        "&patientidmatch=" + patientIdMatch +
                        "&seriesUID=" + seriesUID +
                        "&proxy=" + proxy;
            }

            // Create a URL which will be requested from ConQuest web server
            URL url = new URL(cgiUrl + queryStrings);

            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.connect();

            // Reading output from URL
            DataInputStream inputStream = new DataInputStream(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // Print everything to web page (It is just a viewer)
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                out.println(inputLine);
            }
        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe);
        }
    }

    /**
     * Send a http request to ConQuest web server in order to invoke DICOM WADO image request
     * This way I can direct whole WADO communication via RadPlanBio portal (with authentication)
     *
     * @param request - HttpServletRequest - for checking query string parameters
     * @param response - HttpServletResponse - for creating of image
     */
    @RequestMapping(value="/pacs/dicomImage.faces", method = RequestMethod.GET)
    public void getDicomImage(HttpServletRequest request, HttpServletResponse response) {
        try {
            String contentType = "application/dicom";
            response.setContentType(contentType);

            String cgiUrl = this.getStudySite().getPacs().getPacsBaseUrl();

            // These are the identifications according to which we are going to browse the PACS server
            String studyUID = "";
            String seriesUID = "";
            String objectUID = "";
            String queryStrings;

            // Look for query strings which are necessary for DICOM viewer
            if (request.getParameter("studyUID") != null) {
                studyUID = request.getParameter("studyUID");
            }
            if (request.getParameter("seriesUID") != null) {
                seriesUID =  request.getParameter("seriesUID");
            }
            if (request.getParameter("objectUID") != null) {
                objectUID =  request.getParameter("objectUID");
            }

            queryStrings = "?requestType=WADO" +
                    "&contentType=" + contentType +
                    "&studyUID=" + studyUID +
                    "&seriesUID=" + seriesUID +
                    "&objectUID=" + objectUID;

            // Create a URL which will be requested from ConQuest web server
            URL url = new URL(cgiUrl + queryStrings);

            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.connect();

            // Reading output from URL
            DataInputStream inputStream = new DataInputStream(connection.getInputStream());

            byte[] buffer = new byte[4096];
            int n;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((n = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, n);
            }
            output.close();

            ServletOutputStream sos = response.getOutputStream();
            response.setHeader("Content-Length", String.valueOf(output.size()));
            response.setHeader("Content-Disposition", "attachment; filename=\"dgate.dcm\"");
            sos.write(output.toByteArray());
            sos.flush();
        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe);
        }
    }

    //endregion

    //region Weasis

    /**
     * Return jnlp web start for Weasis
     * @param request request
     * @param response response
     */
    @RequestMapping(value="/pacs/studyView.faces", method = RequestMethod.GET)
    public void showWeasisViewer(HttpServletRequest request, HttpServletResponse response) {
        try {
            String cgiUrl = this.getStudySite().getPacs().getPacsBaseUrl();
            String mode = "";

            // These are the identifications according to which we are going to browse the PACS server
            String patientIdMatch = "";
            String studyUid = "";
            String queryStrings = "";

            // Public URL for proxy (this URL is used in lua code in Conquest in order to generate javascript)
            String proxy = this.getStudySite().getPortal().getPublicUrl();

            // Look in the query string to find which mode to use for ConQuest
            if (request.getParameter("mode") != null) {
                mode = request.getParameter("mode");
            }

            // Look for query strings which are necessary for DICOM viewer
            if (mode.equals("rpbweasisstudyview") || mode.equals("weasisstudyviewer")) {
                if (request.getParameter("patientId") != null) {
                    patientIdMatch = request.getParameter("patientId");
                }
                if (request.getParameter("studyUid") != null) {
                    studyUid =  request.getParameter("studyUid");
                }

                // Store information limited to session
                String sessionId = request.getRequestedSessionId();
                request.getSession().setAttribute("study", patientIdMatch + ":" + studyUid);
                request.getSession().setAttribute("proxy", proxy);
                request.getSession().setAttribute("cgi", cgiUrl);

                queryStrings = "?port=5678" +
                        "&address=127.0.0.1" +
                        "&mode=" + mode +
                        "&session=" + sessionId +
                        "&size=560";
            }

            // Create a URL which will be requested from ConQuest web server
            URL url = new URL(cgiUrl + queryStrings);

            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.connect();

            // Reading output from URL
            DataInputStream inputStream = new DataInputStream(connection.getInputStream());

            byte[] buffer = new byte[4096];
            int n;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((n = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, n);
            }
            output.close();

            ServletOutputStream sos = response.getOutputStream();
            response.setContentType("application/x-java-jnlp-file");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + patientIdMatch + ":" + studyUid + ".jnlp\"");
            sos.write(output.toByteArray());
            sos.flush();
        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe);
        }
    }

    /**
     * Return XML for Weasis which will do WADO request from Weasis viewer
     * @param request request
     * @param response response
     */
    @RequestMapping(value="/pacs/viewXml.faces", method = RequestMethod.GET)
    public void showDicomImageXml(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("text/xml");
            PrintWriter out = response.getWriter();

            // mode = rpbweasisstudyxml || rpbweasisseriesxml
            String mode = "";
            if (request.getParameter("mode") != null) {
                mode = request.getParameter("mode");
            }
            String sessionId = "";
            if (request.getParameter("sessionid") != null) {
                sessionId = request.getParameter("sessionid");
            }

            String proxy = HttpSessionCollector.find(sessionId).getAttribute("proxy").toString();
            String cgiUrl = HttpSessionCollector.find(sessionId).getAttribute("cgi").toString();

            String queryStrings = "?port=5678" +
                    "&address=127.0.0.1" +
                    "&mode=" + mode;

            if (mode.equals("rpbweasisstudyxml")) {
                // patient ":" study
                queryStrings += "&study=" + HttpSessionCollector.find(sessionId).getAttribute("study").toString();
            }
            else if (mode.equals("rpbweasisseriesxml")) {
                // patient ":" series
                queryStrings += "&series=" + HttpSessionCollector.find(sessionId).getAttribute("series").toString();
            }

            queryStrings += "&dum=.xml";
            queryStrings += "&session=" + sessionId;
            queryStrings += "&proxy=" + proxy;

            // Create a URL which will be requested from ConQuest web server
            URL url = new URL(cgiUrl + queryStrings);

            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.connect();

            // Reading output from URL
            DataInputStream inputStream = new DataInputStream(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // Print everything to web page (It is just a viewer)
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                out.println(inputLine);
            }
        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe);
        }
    }

    /**
     * Make Weasis WADO request for DICOM file and return it to viewer
     * @param request request
     * @param response response
     */
    @RequestMapping(value="/pacs/wado.faces", method = RequestMethod.GET)
    public void getWadoImage(HttpServletRequest request, HttpServletResponse response) {
        try {
            String contentType = "";
            if (request.getParameter("contentType") != null) {
                contentType = request.getParameter("contentType");
            }
            response.setContentType(contentType);

            String rurl = request.getRequestURL().toString();
            String sessionId = rurl.substring(rurl.indexOf("sessionid=") + 10, rurl.length());
            String cgiUrl = HttpSessionCollector.find(sessionId).getAttribute("cgi").toString();

            String requestType = "";
            if (request.getParameter("requestType") != null) {
                requestType = request.getParameter("requestType");
            }
            String studyUID = "";
            if (request.getParameter("studyUID") != null) {
                studyUID = request.getParameter("studyUID");
            }
            String seriesUID = "";
            if (request.getParameter("seriesUID") != null) {
                seriesUID =  request.getParameter("seriesUID");
            }
            String objectUID = "";
            if (request.getParameter("objectUID") != null) {
                objectUID =  request.getParameter("objectUID");
            }

            String queryStrings = "?requestType=" + requestType +
                    "&contentType=" + contentType +
                    "&studyUID=" + studyUID +
                    "&seriesUID=" + seriesUID +
                    "&objectUID=" + objectUID;

            // Create a URL which will be requested from ConQuest web server
            URL url = new URL(cgiUrl + queryStrings);

            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.connect();

            // Reading output from URL
            DataInputStream inputStream = new DataInputStream(connection.getInputStream());

            byte[] buffer = new byte[4096];
            int n;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((n = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, n);
            }
            output.close();

            ServletOutputStream sos = response.getOutputStream();
            response.setHeader("Content-Length", String.valueOf(output.size()));
            response.setHeader("Content-Disposition", "attachment; filename=\"dgate.dcm\"");
            sos.write(output.toByteArray());
            sos.flush();
        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe);
        }
    }

    //endregion

    //region Download

    /**
     * Send a http request to ConQuest web server in order to invoke download of the study
     *
     * @param request - HttpServletRequest - for checking query string parameters
     * @param response - HttpServletResponse - for creating of HTML view
     */
    @RequestMapping(value="/pacs/dicomStudyDownload.faces", method = RequestMethod.GET)
    public void downloadDicomStudy(HttpServletRequest request, HttpServletResponse response) {
        try {
            String cgiUrl = this.getStudySite().getPacs().getPacsBaseUrl();
            String mode = "zipstudy";
            String dum = ".zip";

            //AnonymisedPatientID:AnonymisedStudyID
            String study = "";
            if (request.getParameter("study") != null) {
                study = request.getParameter("study");
            }

            // These are the identifications according to which we are going to browse the PACS server
            String queryStrings = "?mode=" + mode +
                    "&study=" + study +
                    "&dum=" + dum;

            URL url = new URL(cgiUrl + queryStrings);

            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.connect();

            // Reading output from URL
            DataInputStream inputStream = new DataInputStream(connection.getInputStream());

            byte[] buffer = new byte[4096];
            int n;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((n = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, n);
            }
            output.close();

            ServletOutputStream sos = response.getOutputStream();
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + study + ".zip\"");
            sos.write(output.toByteArray());
            sos.flush();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe);
        }
    }

    /**
     * Send a http request to ConQuest web server in order to invoke download of the study series
     *
     * @param request - HttpServletRequest - for checking query string parameters
     * @param response - HttpServletResponse - for creating of HTML view
     */
    @RequestMapping(value="/pacs/dicomSeriesDownload.faces", method = RequestMethod.GET)
    public void downloadDicomSeries(HttpServletRequest request, HttpServletResponse response) {
        try {
            String cgiUrl = this.getStudySite().getPacs().getPacsBaseUrl();
            String mode = "zipseries";
            String dum = ".zip";

            //AnonymisedPatientID:AnonymisedSeriesID
            String series = "";
            if (request.getParameter("series") != null) {
                series = request.getParameter("series");
            }
            // These are the identifications according to which we are going to browse the PACS server
            String queryStrings = "?mode=" + mode +
                    "&series=" + series +
                    "&dum=" + dum;

            URL url = new URL(cgiUrl + queryStrings);

            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.connect();

            // Reading output from URL
            DataInputStream inputStream = new DataInputStream(connection.getInputStream());

            byte[] buffer = new byte[4096];
            int n;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((n = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, n);
            }
            output.close();

            ServletOutputStream sos = response.getOutputStream();
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + series + ".zip\"");
            sos.write(output.toByteArray());
            sos.flush();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe);
        }
    }

    //endregion

    //endregion

    //region Private Methods

    /**
     * Use the http request to check version of the browser in order to decide if it is supported by DICOM viewer
     * We presume that it supports unless it is one of the three major and smaller version number than tested
     *
     * @param request - HttpServletRequest - for checking query string parameters
     * @return boolean - support for HTML5 viewer
     */
    private boolean viewerHasSupportForBrowser(HttpServletRequest request) {
        boolean isSupporting = true;

        int minIE = 10;
        int minGecko = 21;
        int minChrome = 26;

        if (BrowserDetection.atLeast(request, BrowserDetection.UserAgent.IE)) {
            if (!BrowserDetection.browserVersionAtLeast(request, BrowserDetection.UserAgent.IE, minIE)) {
                isSupporting = false;
            }
        } else if (BrowserDetection.atLeast(request, BrowserDetection.UserAgent.Gecko)) {
            if (!BrowserDetection.browserVersionAtLeast(request, BrowserDetection.UserAgent.Gecko, minGecko)) {
                isSupporting = false;
            }
        } else if  (BrowserDetection.atLeast(request, BrowserDetection.UserAgent.AppleWebKit)) {
            if (!BrowserDetection.browserVersionAtLeast(request, BrowserDetection.UserAgent.Chrome, minChrome)) {
                isSupporting = false;
            }
        }

        return isSupporting;
    }

    //endregion

}
