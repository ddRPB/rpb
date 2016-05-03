/*

	Copyright 2012 VU Medical Center Amsterdam

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	    http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

*/

package de.dktk.dd.rpb.core.ocsoap.connect;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.BindingProvider;

import de.dktk.dd.rpb.core.ocsoap.soap.EventDefListAllHandler;
import de.dktk.dd.rpb.core.ocsoap.soap.ImportRequestHandler;
import de.dktk.dd.rpb.core.ocsoap.soap.LoggingHandler;
import de.dktk.dd.rpb.core.ocsoap.soap.StudyListAllHandler;
import de.dktk.dd.rpb.core.ocsoap.soap.StudySubjectHandler;
import de.dktk.dd.rpb.core.ocsoap.soap.Util;
import de.dktk.dd.rpb.core.ocsoap.soap.WsseSecurityHandler;
import de.dktk.dd.rpb.core.ocsoap.ws.DataWsService;
import de.dktk.dd.rpb.core.ocsoap.ws.EventWsService;
import de.dktk.dd.rpb.core.ocsoap.ws.StudyEventDefinitionWsService;
import de.dktk.dd.rpb.core.ocsoap.ws.StudySubjectWsService;
import de.dktk.dd.rpb.core.ocsoap.ws.StudyWsService;

/**
 * Supports "low-level" interaction with OpenClinica web service such
 * as exception handling, credentials and soap handler setup.
 *
 * @author Arjan van der Velde (a.vandervelde (at) xs4all.nl) - original
 * @author tomas@skripcak.net - enhanced
 * @since 12 Jun 2013
 */
public class OCConnector {

    // region Finals

	/** Status message for failure (should correspond to OpenClinica "result" value */
	public static final String STATUS_FAIL = "Fail";
	/** Status message for success (should correspond to OpenClinica "result" value */
	public static final String STATUS_SUCCESS = "Success";

	/** data wsdl location, relative to baseURL */
	public static final String URL_DATA = "ws/data/v1/dataWsdl.wsdl";
	/** event wsdl location, relative to baseURL */
	public static final String URL_EVENT = "ws/event/v1/eventWsdl.wsdl";
	/** study wsdl location, relative to baseURL */
	public static final String URL_STUDY = "ws/study/v1/studyWsdl.wsdl";
	/** studyEventDefinition wsdl location, relative to baseURL */
	public static final String URL_STUDYEVENTDEF = "ws/studyEventDefinition/v1/studyEventDefinitionWsdl.wsdl";
	/** studySubject wsdl location, relative to baseURL */
	public static final String URL_STUDYSUBJECT = "ws/studySubject/v1/studySubjectWsdl.wsdl";

    //endregion

    // region Members

    /** return status of last web service call */
    private String status;
    /** messages returned during web service calls */
    private ArrayList<String> messages;
    /** base url for OpenClinica WS instance */
    private String baseURL;

	/** Flag to turn on/off logging */
	private boolean logging;
	/** This class is a singleton, so keep a static instance */
	private static OCConnector instance;
	/** WS binding: Study */
	protected org.openclinica.ws.study.v1.Ws studyBinding;
	/** WS binding: Study Subject */
	protected org.openclinica.ws.studysubject.v1.Ws studySubjectBinding;
	/** WS binding: Event */
	protected org.openclinica.ws.event.v1.Ws eventBinding;
	/** WS binding: Data */
	protected org.openclinica.ws.data.v1.Ws dataBinding;
	/** WS binding: Study Event Definition */
	protected org.openclinica.ws.studyeventdefinition.v1.Ws studyEventDefinitionBinding;
	/** SOAP Message Handler for WSSE Security */
	private WsseSecurityHandler wsseHandler;
	/** Data type factory for XML */
	protected DatatypeFactory dataTypeFactory;

    //endregion

    //region getInstance

	/**
	 * get current instance or create new if not yes instantiated.
	 * @param connectInfo OpenClinica connection info and credentials.
	 * @return returns OCConnector instance
	 * @throws MalformedURLException
	 * @throws ParserConfigurationException
	 * @throws DatatypeConfigurationException
	 */
	public static OCConnector getInstance(ConnectInfo connectInfo)
			throws MalformedURLException, ParserConfigurationException, DatatypeConfigurationException {
		return getInstance(connectInfo, false, false);
	}

	/**
	 * get current instance or create new if not yet instantiated. if logging
	 * option differs from logging option in existing instance, a new one will
	 * be created.
	 * @param connectInfo OpenClinica connection info and credentials.
	 * @param logging toggle logging
	 * @return returns OCConnector instance
	 * @throws MalformedURLException
	 * @throws ParserConfigurationException
	 * @throws DatatypeConfigurationException
	 */
	public static OCConnector getInstance(ConnectInfo connectInfo, boolean logging)
			throws MalformedURLException, ParserConfigurationException, DatatypeConfigurationException {
		return getInstance(connectInfo, logging, false);
	}

	/**
	 * get current instance or create new if not yet instantiated. if logging
	 * option differs from logging option in existing instance or if forceInstantiation
	 * is set, a new one instance be created.
	 * @param connectInfo OpenClinica connection info and credentials.
	 * @param logging toggle logging
	 * @param forceInstantiation if set force the creation of a new instance
	 * @return returns OCConnector instance
	 * @throws MalformedURLException
	 * @throws ParserConfigurationException
	 * @throws DatatypeConfigurationException
	 */
	public static OCConnector getInstance(ConnectInfo connectInfo, boolean logging, boolean forceInstantiation)
            throws MalformedURLException, ParserConfigurationException, DatatypeConfigurationException {
		if (forceInstantiation || instance == null || instance.isLogging() != logging) {
			instance = new OCConnector(connectInfo, logging);
		}
		return instance;
	}

    //endregion

    //region Constructors

	/**
	 * Create instance (we are a singleton, so, protected), called via getInstance method
	 * @throws DatatypeConfigurationException 
	 */
	protected OCConnector() throws DatatypeConfigurationException {
		dataTypeFactory = DatatypeFactory.newInstance();
		status = STATUS_FAIL;
		messages = new ArrayList<String>();
		Util.installPermissiveTrustManager();
		wsseHandler = new WsseSecurityHandler();
	}

	/**
	 * Create instance (we are a singleton, so, protected), called via getInstance method
	 * @param connectInfo OpenClinica connection info and credentials.
	 * @param logging toggle logging
	 * @throws MalformedURLException 
	 * @throws ParserConfigurationException 
	 * @throws DatatypeConfigurationException 
	 */
	protected OCConnector(ConnectInfo connectInfo, boolean logging)
			throws MalformedURLException, ParserConfigurationException, DatatypeConfigurationException {
		this();
		String baseURL = connectInfo.getBaseURL();
		if (baseURL.charAt(baseURL.length() - 1) != '/') {
			this.baseURL = baseURL + "/";
		} else {
			this.baseURL = baseURL;
		}
		setCredentials(connectInfo);
		this.logging = logging;
		setupBindings();
	}

    // endregion

    //region Properties

	/**
	 * get logging flag
	 * @return logging
	 */
	public boolean isLogging() {
		return logging;
	}

	/**
	 * get baseURL
	 * @return baseURL
	 */
	public String getBaseURL() {
		return baseURL;
	}

	/**
	 * get current status
	 * @return status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Check whether last action was successful
	 * @return true if successful
	 */
	public boolean isSuccess() {
		return status.equals(STATUS_SUCCESS);
	}

	/**
	 * Get messages
	 * @return messages
	 */
	public ArrayList<String> getMessages() {
		return messages;
	}

	/**
	 * Set credentials for web service
	 * @param connectInfo user, password hash and url
	 */
	public void setCredentials(ConnectInfo connectInfo) {
		wsseHandler.setUsername(connectInfo.getUserName());
		wsseHandler.setPassword(connectInfo.getPasswordHash());
		baseURL = connectInfo.getBaseURL();
	}

    //endregion

    //region Methods

	/**
	 * Process status of last action and throw exception if appropriate
	 * @param status status
	 * @param errors list of error messages
	 * @throws OCConnectorException 
	 */
	protected void checkResponseExceptions(String status, List<String> errors) throws OCConnectorException {
		if (status.equals(STATUS_FAIL)) {
			StringBuilder msg = new StringBuilder();
			for (String error : errors) {
				msg.append(error).append("\n");
			}
			throw new OCConnectorException("An error was returned by OpenClinica web service:\n" + msg.toString());
		}
	}

	/**
	 * Configure all WS bindings
     *
     * While having openclinica web service on http tomcat but accessing via reverse https proxy
     *
     * The libraries that the wsdl2java and wsimport stub generators use to do the webservice communication don't allow
     * a http to https redirect follow due to security reasons.
     * So even though I was generating the stubs from a https wsdl location ie. https://wsdl.location.com?wsdl,
     * when I ran the code to do a webservice call, it was trying to make the call to http://wsdl.location.com which
     * resulted in a redirect request to https://wsdl.location.com, But the http library does not allow that.
     * So it just forwards the 302 http code up as an exception.
     *
	 * @throws MalformedURLException 
	 * @throws ParserConfigurationException 
	 */
	private void setupBindings() throws MalformedURLException, ParserConfigurationException {
        studyBinding = new StudyWsService(baseURL + URL_STUDY).getWsSoap11();

        // Make sure if the end point you need point to is on https, that you are making a direct https call and not a http call that will ask for an https redirect
        // In this case make sure that endpoint address is the same as I configure it depending on settings stored in DB
        BindingProvider studyBindingProvider = (BindingProvider) studyBinding;
        if (!studyBindingProvider.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY).equals(baseURL + URL_STUDY)) {
            studyBindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseURL + URL_STUDY);
        }

		studySubjectBinding = new StudySubjectWsService(baseURL + URL_STUDYSUBJECT).getWsSoap11();
        BindingProvider studySubjectBindingProvider = (BindingProvider) studySubjectBinding;
        if (!studySubjectBindingProvider.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY).equals(baseURL + URL_STUDYSUBJECT)) {
            studySubjectBindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseURL + URL_STUDYSUBJECT);
        }

        eventBinding = new EventWsService(baseURL + URL_EVENT).getWsSoap11();
        BindingProvider eventBindingProvider = (BindingProvider) eventBinding;
        if (!eventBindingProvider.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY).equals(baseURL + URL_EVENT)) {
            eventBindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseURL + URL_EVENT);
        }

		dataBinding = new DataWsService(baseURL + URL_DATA).getWsSoap11();
        BindingProvider dataBindingProvider = (BindingProvider) dataBinding;
        if (!dataBindingProvider.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY).equals(baseURL + URL_DATA)) {
            dataBindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseURL + URL_DATA);
        }

        StudyEventDefinitionWsService ws = new StudyEventDefinitionWsService(baseURL + URL_STUDYEVENTDEF);
		studyEventDefinitionBinding = ws.getWsSoap11();
        BindingProvider studyEventDefinitionBindingProvider = (BindingProvider) studyEventDefinitionBinding;
        if (!studyEventDefinitionBindingProvider.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY).equals(baseURL + URL_STUDYEVENTDEF)) {
            studyEventDefinitionBindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseURL + URL_STUDYEVENTDEF);
        }

		// setup handlers 'n stuff
		setupDefaultHandlers(studyBinding);
		setupDefaultHandlers(studySubjectBinding);
		setupDefaultHandlers(eventBinding);
		setupDefaultHandlers(dataBinding);
		setupDefaultHandlers(studyEventDefinitionBinding);

		// turn off the cleaning feature, I will provide only well formed xml string for import
		boolean cleanOdmWithTransformation = false;
		ImportRequestHandler importRequestHandler = new ImportRequestHandler(messages, cleanOdmWithTransformation);
		Util.addMessageHandler(dataBinding, 0, importRequestHandler);

		StudyListAllHandler studyListAllHandler = new StudyListAllHandler(messages);
		Util.addMessageHandler(studyBinding, 0, studyListAllHandler);
		StudySubjectHandler studySubjectHandler = new StudySubjectHandler(messages);
		Util.addMessageHandler(studySubjectBinding, 0, studySubjectHandler);
		EventDefListAllHandler eventDefListAllHandler = new EventDefListAllHandler(messages);
		Util.addMessageHandler(studyEventDefinitionBinding, 0, eventDefListAllHandler);
	}

	/**
	 * Install default SOAP message handlers
	 * @param binding binding
	 */
	private void setupDefaultHandlers(Object binding) {
		if (logging) {
			Util.addMessageHandler(binding, new LoggingHandler());
		}
		Util.addMessageHandler(binding, 0, wsseHandler.newInstance());
	}

	/**
	 * Clears messages
	 */
	public void clearMessages() {
		messages = new ArrayList<String>();
	}

    //endregion

}
