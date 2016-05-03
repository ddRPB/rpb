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

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Logger;

/**
 * A quick and dirty logging handler of SOAP messages.
 *
 * @author Arjan van der Velde (a.vandervelde (at) xs4all.nl)
 * @author Tomas Skripcak (small enhancements)
 */
public class LoggingHandler implements SOAPHandler<SOAPMessageContext> {

	//region Finals

	private static final Logger log = Logger.getLogger(LoggingHandler.class);

	//endregion

	//region Constructors

	/**
	 * Create the handler
	 */
	public LoggingHandler() {
		super();
	}

	//endregion

	//region Overrides

	@Override
	public boolean handleMessage(SOAPMessageContext c) {

		SOAPMessage msg = c.getMessage();
		boolean request = ((Boolean) c.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY));
		try {
			if (request) { // This is a request message.
				// Write the message to the output stream
				log.debug("Request:\n"
						+ Util.documentToString(msg.getSOAPBody().getOwnerDocument()));
			}
			else { // This is the response message
				log.debug("Response:\n"
						+ Util.documentToString(msg.getSOAPBody().getOwnerDocument()));
			}
		}
		catch (Exception e) {
			log.error(e);
		}

		return Boolean.TRUE;
	}

	@Override
	public boolean handleFault(SOAPMessageContext c) {

		SOAPMessage msg = c.getMessage();
		try {
			log.debug("Fault:\n"
					+ Util.documentToString(msg.getSOAPBody().getOwnerDocument()));
		} catch (Exception e) {
			log.error(e);
		}

		return Boolean.TRUE;
	}

	@Override
	public void close(MessageContext c) {
		// NOOP
	}

	@Override
	public Set<QName>  getHeaders() {
		// NOOP
		return null;
	}

	//endregion

}
