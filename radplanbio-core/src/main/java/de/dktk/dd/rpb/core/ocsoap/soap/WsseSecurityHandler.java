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

package de.dktk.dd.rpb.core.ocsoap.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;

/**
 * Message handler that adds a WSSE security section to the SOAP Header.
 * @author Arjan van der Velde (a.vandervelde (at) xs4all.nl)
 * @author tomas@skripcak.net - enhanced
 */
public final class WsseSecurityHandler implements SOAPHandler<SOAPMessageContext> {

	//region Finals

	private static final Logger log = LoggerFactory.getLogger(WsseSecurityHandler.class);

	//endregion

	//region Members

	/** user name */
	private String username;
	/** password */
	private String password;

	//endregion

	//region Constructors

	/**
	 * Construct the handler with empty username and empty password
	 */
	public WsseSecurityHandler() {
		setUsername("");
		setPassword("");
	}

	/**
	 * Construct the handler setting the username and password
	 * @param username username to be set
	 * @param password password to be set
	 */
	public WsseSecurityHandler(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	//endregion

	//region Properties

	/**
	 * Get the username
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Set the username
	 * @param username username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Get the password
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set the password
	 * @param password password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	//endregion

	//region Methods

	/**
	 * Returns a clone
	 * @return A new instance using same username and password
	 */
	public WsseSecurityHandler newInstance() {
		return new WsseSecurityHandler(username, password);
	}

	//endregion

	//region Overrides

	@Override
	public boolean handleMessage(final SOAPMessageContext msgCtx) {
		Boolean result = Boolean.TRUE;

		// Indicator telling us which direction this message is going in
		final Boolean outInd = (Boolean) msgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		// Handler must only add security headers to outbound messages
		if (outInd) {
			try {
				// Get the SOAP Envelope
				final SOAPEnvelope envelope = msgCtx.getMessage().getSOAPPart().getEnvelope();
				// Header may or may not exist yet
				SOAPHeader header = envelope.getHeader();
				if (header == null)
					header = envelope.addHeader();
				// Add WSS Usertoken Element Tree
				final SOAPElement security = header.addChildElement("Security", "wsse",
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
				final SOAPElement userToken = security.addChildElement("UsernameToken", "wsse");
				userToken.addChildElement("Username", "wsse").addTextNode(this.username);
				userToken.addChildElement("Password", "wsse").addTextNode(this.password);
			}
			catch (final Exception e) {
				log.warn(e.getMessage(),e);
				result = Boolean.FALSE;
			}
		}

		return result;
	}

	@Override
	public void close(MessageContext arg0) {
		// Nothing required here...
	}

	@Override
	public boolean handleFault(SOAPMessageContext arg0) {
		// Nothing required here...
		return false;
	}

	@Override
	public Set<QName> getHeaders() {
		// Nothing required here...
		return null;
	}

	//endregion

}
