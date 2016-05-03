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

    /** site name (unique protocol ident, study identifier) */
	private String siteName;
    /** real site name which has descriptive values for user */
    private String realSiteName;

	/** study name */
	private String studyName;
	/** study oid */
	private String studyOID;
    /** study identifier*/
    private String studyIdentifier;
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
	 * Create a study from an OC StudyType object
	 * @param ocStudy OC StudyType
	 */
	public Study(StudyType ocStudy) {
		this.studyName = ocStudy.getName();
		this.studyOID = ocStudy.getOid();
        this.studyIdentifier = ocStudy.getIdentifier();
	}

    //endregion

    //region Properties

    /**
	 * Get siteName
	 * @return siteName
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * Set site name
	 * @param siteName site name
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
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
	@SuppressWarnings("unused")
    public void setStudyIdentifier(String studyIdentifier) {
        this.studyIdentifier = studyIdentifier;
    }

    /**
     * Determine whether study is multi-centre
     * @return True when study is multi-centre
     */
    public boolean isMulticentric() {
        return this.hasSiteName() && !this.getSiteName().equals(this.getStudyIdentifier());
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

	/**
	 * Check whether or not site name is defined
	 * @return has sitename or not (bool)
	 */
	public boolean hasSiteName() {
		return !(siteName == null || siteName.equals(""));
	}

    //endregion

    //region Overrides

    @Override
	public String toString() {
		return "Study: studyName: " + studyName + ", siteName: " + siteName + ", studyOID: " + studyOID     + ", studyIdentifier: " + studyIdentifier
				+ ", Defined events: " + events + ", Study Subjects: " + studySubjects;
	}

	@Override
	public int hashCode() {
		return (this.studyName + this.studyOID + this.studyIdentifier).hashCode();
	}

    //endregion
}
