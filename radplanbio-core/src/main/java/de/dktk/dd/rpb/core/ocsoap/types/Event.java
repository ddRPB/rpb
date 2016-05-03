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

package de.dktk.dd.rpb.core.ocsoap.types;

import javax.xml.datatype.DatatypeConfigurationException;

import org.openclinica.ws.beans.EventType;

/**
 * Simple representation of an Event
 * @author Arjan van der Velde (a.vandervelde (at) xs4all.nl) - original
 * @author tomas@skripcak.net - enhanced
 * @since 11 Jun 2013
 */
public class Event {

    //region Members

    /** event OID */
    protected String eventOID;
    /** name of the event */
	protected String eventName;
    protected String description;
    protected String category;
    protected String type;
    protected boolean isRepeating;

    //endregion

    //region Constructors

    /**
	 * create a new event
	 * @throws DatatypeConfigurationException 
	 */
	public Event() throws DatatypeConfigurationException {
	}

	/**
	 * create a new event from an OC EventType object
	 * @param event OC event to initialize from
	 * @throws DatatypeConfigurationException 
	 */
	public Event(EventType event) throws DatatypeConfigurationException {
		this();
		setEventOID(event.getEventDefinitionOID());
	}

    //endregion

    //region Properties

    /**
     * Get the event OID
     * @return event OID
     */
    public String getEventOID() {
        return this.eventOID;
    }

    /**
     * Set the event OID
     * @param eventOID event OID
     */
    public void setEventOID(String eventOID) {
        this.eventOID = eventOID;
    }

    /**
	 * Get the event name
	 * @return event name
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * Set the event name
	 * @param eventName event name
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getCategory() { return this.category; }

    public void setCategory(String value) {
        this.category = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public boolean getIsRepeating() {
        return this.isRepeating;
    }

    public void setIsRepeating(boolean value) {
        this.isRepeating = value;
    }

    //endregion

    //region Overrides

    @Override
	public String toString() {
		return "Event: eventName: " + eventName + ", eventOID: " + eventOID;
	}

    //endregion

}
