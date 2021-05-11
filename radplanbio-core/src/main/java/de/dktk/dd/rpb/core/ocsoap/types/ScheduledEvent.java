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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import de.dktk.dd.rpb.core.domain.edc.EventData;

import de.dktk.dd.rpb.core.util.Constants;
import org.openclinica.ws.beans.EventType;

/**
 * Simple representation of a Scheduled Event
 *
 * @author Arjan van der Velde (a.vandervelde (at) xs4all.nl)
 * @author tomas@skripcak.net - enhanced
 * @since 23 Oct 2014
 */
public class ScheduledEvent extends Event {

    //region Members

	/** XML Data type factory */
	private DatatypeFactory dataTypeFactory;

	private XMLGregorianCalendar startDate;
	private XMLGregorianCalendar startTime;
	private XMLGregorianCalendar endDate;
	private XMLGregorianCalendar endTime;

    private String status;
    private int studyEventRepeatKey;

    //endregion

    //region Constructors

	/**
	 * Create a new event
	 *
	 * @throws DatatypeConfigurationException DatatypeConfigurationException
	 */
	public ScheduledEvent() throws DatatypeConfigurationException {
		super();
		dataTypeFactory = DatatypeFactory.newInstance();
		startDate = dataTypeFactory.newXMLGregorianCalendar(new GregorianCalendar());
		startDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
	}

	/**
	 * Create a new event from an Event (not scheduled)
	 *
	 * @param event event
	 * @throws DatatypeConfigurationException DatatypeConfigurationException
	 */
	public ScheduledEvent(Event event) throws DatatypeConfigurationException {
		this();
		this.eventOID = event.getEventOID();
		this.eventName = event.getEventName();
	}

	/**
	 * Create a new event from and EventData (not scheduled)
	 * @param eventData eventData
	 * @throws DatatypeConfigurationException DatatypeConfigurationException
	 */
	public ScheduledEvent(EventData eventData) throws DatatypeConfigurationException {
		this();

		// Event OID is mandatory
		this.eventOID= eventData.getStudyEventOid();

		// When even repeat key is available use it
		if (eventData.getStudyEventRepeatKey() != null && !eventData.getStudyEventRepeatKey().isEmpty()) {
			this.studyEventRepeatKey = Integer.parseInt(eventData.getStudyEventRepeatKey());
		}

		// Event start date is mandatory
		GregorianCalendar c = new GregorianCalendar();

		// When event start date is available use it
		if (eventData.getStartDate() != null && !eventData.getStartDate().isEmpty()) {
			c.setTime(eventData.getComparableStartDate());
		}
		// Otherwise it will be current date
		else {
			c.setTime(new Date());
		}

		// Use a setter to execute logic to get rid of timezone
		this.setStartDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
	}
	
	/**
	 * Create a new event from an OC EventType event
	 *
	 * @param event the OC event to initialize from
	 * @throws DatatypeConfigurationException DatatypeConfigurationException
	 */
	public ScheduledEvent(EventType event) throws DatatypeConfigurationException {
		this();
		setStartDate(event.getStartDate());
		setEndDate(event.getEndDate());
		setEventOID(event.getEventDefinitionOID());
	}

    //endregion

    //region Properties

	//region StartDate

	/**
	 * Get event start date
	 * @return event start date
	 */
	public XMLGregorianCalendar getStartDate() {
		return this.startDate;
	}

	/**
	 * Set event start date
	 * @param startDate event start date
	 */
	public void setStartDate(XMLGregorianCalendar startDate) {
		this.startDate = startDate;
		if (this.startDate != null) {
			this.startDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		}
	}

	/**
	 * Set event start date from String
	 * @param startDate event start date as String (yyyy-mm-dd)
	 */
	public void setStartDate(String startDate) {

		DateFormat dateFormat = new SimpleDateFormat(Constants.OC_DATEFORMAT);
		Date date = null;
		try {
			date = dateFormat.parse(startDate);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		GregorianCalendar cal = new GregorianCalendar();
		if (date != null) {
			cal.setTime(date);
		}

		this.setStartDate(
				dataTypeFactory.newXMLGregorianCalendar(
						cal.get(Calendar.YEAR),
						cal.get(Calendar.MONTH)+1,
						cal.get(Calendar.DAY_OF_MONTH),
						DatatypeConstants.FIELD_UNDEFINED,
						DatatypeConstants.FIELD_UNDEFINED,
						DatatypeConstants.FIELD_UNDEFINED,
						DatatypeConstants.FIELD_UNDEFINED,
						DatatypeConstants.FIELD_UNDEFINED
				)
		);
	}

	/**
	 * Converts XMLGregorianCalendar to java.util.Date in Java
	 */
	public Date getComparableStartDate(){
		return this.startDate != null ? this.startDate.toGregorianCalendar().getTime() : null;
	}

	//endregion

	//region StartTime

	public XMLGregorianCalendar getStartTime() {
		return this.startTime;
	}

	public void setStartTime(XMLGregorianCalendar startTime) {
		this.startTime = startTime;
		if (this.startTime != null) {
			this.startTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		}
	}

	public void setStartTime(String startTime) {

		DateFormat dateFormat = new SimpleDateFormat(Constants.OC_TIMEFORMAT);
		Date date = null;
		try {
			date = dateFormat.parse(startTime);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		GregorianCalendar cal = new GregorianCalendar();
		if (date != null) {
			cal.setTime(date);
		}

		this.setStartTime(
			dataTypeFactory.newXMLGregorianCalendar(
				this.startDate.getYear(),
				this.startDate.getMonth(),
				this.startDate.getDay(),
				cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE),
				0, // seconds
				DatatypeConstants.FIELD_UNDEFINED,
				DatatypeConstants.FIELD_UNDEFINED
			)
		);
	}

	//endregion

	//region EndDate

	/**
	 * Get event end date
	 * @return end date
	 */
	public XMLGregorianCalendar getEndDate() {
		return this.endDate;
	}

	/**
	 * Set event end date
	 * @param endDate event end date
	 */
	public void setEndDate(XMLGregorianCalendar endDate) {
		this.endDate = endDate;
		if (this.endDate != null) {
			this.endDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		}
	}

	/**
	 * Set event end date from String
	 * @param endDate event end date as String (yyyy-mm-dd)
	 */
	public void setEndDate(String endDate) {
		this.setEndDate(dataTypeFactory.newXMLGregorianCalendar(endDate));
	}

	/**
	 * Converts XMLGregorianCalendar to java.util.Date in Java
	 */
	public Date getComparableEndDate(){
		return this.endDate != null ? this.endDate.toGregorianCalendar().getTime() : null;
	}

	//endregion

	//region EndTime

	public XMLGregorianCalendar getEndTime() {
		return this.endTime;
	}

	public void setEndTime(XMLGregorianCalendar endTime) {
		this.endTime = endTime;
		if (this.endTime != null) {
			this.endTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		}
	}

	public void setEndTime(String endTime) {
		this.setEndTime(dataTypeFactory.newXMLGregorianCalendar(endTime));
	}

	//endregion

	//region RepeatKey

    public int getStudyEventRepeatKey() {
        return this.studyEventRepeatKey;
    }

    public void setStudyEventRepeatKey(int value) {
        if (this.studyEventRepeatKey != value) {
            this.studyEventRepeatKey = value;
        }
    }

    //endregion

    //region Status

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    //endregion

    //endregion

    //region Overrides

	@Override
	public String toString() {
		return "ScheduledEvent: eventName: " + eventName + ", eventOID: " + eventOID + ", startDate: " + startDate
				+ ", endDate: " + endDate;
	}

    //endregion

}
