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

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Connection credentials and URL. Also facilitates the SHA1 hashing of the password
 *
 * @author Arjan van der Velde (a.vandervelde (at) xs4all.nl)
 * @author tomas@skripcak.net - enhanced
 */
public class ConnectInfo {

	//region Members

	/** base url of openclinica web service (i.e. https://www.example.com/OpenClinica-ws/) */
	private String baseURL;
	/** user name to use when calling web service methods */
	private String userName;
	/** password hash to use when calling web service methods */
	private String passwordHash;

	//endregion

	//region Constructors

	/**
	 * Constructs a ConnectionInfo object from a url, username and password hash
	 * @param baseURL base url of openclinica web service (i.e. https://www.example.com/OpenClinica-ws/)
	 * @param userName user name to use when calling web service methods
	 * @param passwordHash password hash to use when calling web service methods
	 */
	public ConnectInfo(String baseURL, String userName, String passwordHash) {
		super();
		this.baseURL = baseURL;
		this.userName = userName;
		this.passwordHash = passwordHash;
		if (baseURL.charAt(baseURL.length() - 1) != '/') {
			this.baseURL = baseURL + "/";
		} else {
			this.baseURL = baseURL;
		}
	}

	/**
	 * Constructs a ConnectionInfo object from a url and username. Password needs to be set separately.
	 * @param baseURL base url of openclinica web service (i.e. https://www.example.com/OpenClinica-ws/)
	 * @param userName user name to use when calling web service methods
	 */
	public ConnectInfo(String baseURL, String userName) {
		super();
		this.baseURL = baseURL;
		this.userName = userName;
		if (baseURL.charAt(baseURL.length() - 1) != '/') {
			this.baseURL = baseURL + "/";
		} else {
			this.baseURL = baseURL;
		}
	}

	//endregion

	//region Properties

	/**
	 * Get the base url of openclinica web service.
	 * @return base url of openclinica web service */
	public String getBaseURL() {
		return baseURL;
	}

	/** 
	 * Set the base url of openclinica web service
	 * @param baseURL baseURL base url of openclinica web service (i.e. https://www.example.com/OpenClinica-ws/)
	 * */
	@SuppressWarnings("unused")
	public void setBaseURL(String baseURL) {
		if (baseURL.charAt(baseURL.length() - 1) != '/') {
			this.baseURL = baseURL + "/";
		} else {
			this.baseURL = baseURL;
		}
	}

	/**
	 * Get the OpenClinica user name
	 * @return OpenClinica user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Set the OpenClinica user name
	 * @param userName OpenClinica user name
	 */
	@SuppressWarnings("unused")
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Get the password hash (in ASCII format)
	 * @return password hash
	 */
	public String getPasswordHash() {
		return passwordHash;
	}

	/**
	 * Set the password hash (in ASCII format)
	 * @param passwordHash password hash in ASCII format
	 */
	@SuppressWarnings("unused")
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	/**
	 * Set the password SHA1 (hash) from a plain-text password.
	 * @param password plain-text password to be hashed
	 */
	public void setPassword(String password) {
		this.passwordHash = DigestUtils.shaHex(password);
	}

	//endregion

	//region Overrides

	@Override
	public String toString() {
		return "ConnectInfo: baseURL: " + baseURL + ", userName: " + userName + ", passwordHash: " + passwordHash;
	}

	//endregion

}
