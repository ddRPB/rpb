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

import java.util.ArrayList;

import org.openclinica.ws.beans.SiteType;
import org.openclinica.ws.beans.StudyType;

/**
 * Simple representation of a Study
 *
 * @author Arjan van der Velde (a.vandervelde (at) xs4all.nl) - original
 * @author tomas@skripcak.net - enhanced
 * @since 11 Jun 2013
 */
public class Study {

    //region Members

	/** study name */
	private String studyName;
	/** study oid */
	private String studyOID;
    /** study identifier*/
    private String studyIdentifier;

	/** real site name which has descriptive values for user */
	private String realSiteName;
	/** study site oid */
	private String siteOID;
	/** unique protocol study site identifier */
	private String siteIdentifier;

	/** events defined for this study */
	private ArrayList<Event> events;
	/** subjects in this study */
	private ArrayList<StudySubject> studySubjects;
	
    //endregion

    //region Constructors

    /** create an empty study */
	public Study() {
        // NOOP
	}

	/**
	 * Create a parent study from an OC StudyType object
	 * @param parentStudyType OC StudyType
	 */
	public Study(StudyType parentStudyType) {
		this.studyName = parentStudyType.getName();
		this.studyOID = parentStudyType.getOid();
        this.studyIdentifier = parentStudyType.getIdentifier();
	}

	/**
	 * Create site study from and OC StudyType and OC SiteType objects
	 * @param parentStudyType OC StudyType
	 * @param siteType OC SiteType
	 */
	public Study(StudyType parentStudyType, SiteType siteType) {
		this(parentStudyType);

		this.realSiteName = siteType.getName();
		this.siteOID = siteType.getIdentifier();
		this.siteIdentifier = siteType.getIdentifier();
	}

    //endregion

    //region Properties

	/**
	 * Get study name (unique identifier kind of name)
	 * @return study name
	 */
	public String getStudyName() {
		return studyName;
	}

	/**
	 * Set study name (unique identifier kind of name)
	 * @param studyName study name
	 */
	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	/**
	 * Get study OID
	 * @return study OID
	 */
	public String getStudyOID() {
		return studyOID;
	}

	/**
	 * Set study OID
	 * @param studyOID studyOID
	 */
	public void setStudyOID(String studyOID) {
		this.studyOID = studyOID;
	}

    /**
     * Get study Identifier
     * @return study Identifier
     */
    public String getStudyIdentifier() {
        return studyIdentifier;
    }

    /**
     * Set study Identifier
     * @param studyIdentifier studyIdentifier
     */
    public void setStudyIdentifier(String studyIdentifier) {
        this.studyIdentifier = studyIdentifier;
    }

	/**
	 * Get real siteName
	 * @return real siteName
	 */
	public String getRealSiteName() {
		return this.realSiteName;
	}

	/**
	 * Set real site name
	 * @param value real site name
	 */
	public void setRealSiteName(String value) {
		this.realSiteName = value;
	}
	
	public String getSiteOID() {
		return this.siteOID;
	}

	public void setSiteOID(String siteOID) {
		this.siteOID = siteOID;
	}

	/**
	 * Get site identifier
	 * @return site identifier
	 */
	public String getSiteName() {
		return siteIdentifier;
	}

	/**
	 * Set site identifier
	 * @param siteIdentifier site identifier
	 */
	public void setSiteName(String siteIdentifier) {
		this.siteIdentifier = siteIdentifier;
	}
	
	/**
	 * Get events defined for this study
	 * @return events defined for this study
	 */
	public ArrayList<Event> getEvents() {
		if (events == null) {
			events = new ArrayList<Event>();
		}
		return events;
	}

	/**
	 * Set events defined for this study
	 * @param events events for this study
	 */
	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}

	/**
	 * Get subjects in this study
	 * @return list of subjects
	 */
	public ArrayList<StudySubject> getStudySubjects() {
		if (studySubjects == null) {
			studySubjects = new ArrayList<StudySubject>();
		}
		return studySubjects;
	}

	/**
	 * Set list of subjects in this study
	 * @param studySubjects subjects in this study
	 */
	public void setStudySubjects(ArrayList<StudySubject> studySubjects) {
		this.studySubjects = studySubjects;
	}

    //endregion

	//region Methods

	/**
	 * Determine whether study is multi-centre
	 * @return True when study is multi-centre
	 */
	public boolean isMulticentric() {
		return this.hasSiteIdentifier() && !this.getSiteName().equals(this.getStudyIdentifier());
	}

	/**
	 * Check whether or not site identifier is defined
	 * @return has site identifier or not (bool)
	 */
	private boolean hasSiteIdentifier() {
		return this.siteIdentifier != null && !this.siteIdentifier.isEmpty();
	}

	//endregion

    //region Overrides

    @Override
	public String toString() {
		return "Study: studyName: " + studyName + ", siteIdentifier: " + siteIdentifier + ", studyOID: " + studyOID     + ", studyIdentifier: " + studyIdentifier
				+ ", Defined events: " + events + ", Study Subjects: " + studySubjects;
	}

	@Override
	public int hashCode() {
		return (this.studyName + this.studyOID + this.studyIdentifier).hashCode();
	}

    //endregion
}
