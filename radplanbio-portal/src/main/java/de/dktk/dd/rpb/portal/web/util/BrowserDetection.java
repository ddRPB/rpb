/*
 * Copyright 2005 Joe Walker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dktk.dd.rpb.portal.web.util;

import javax.servlet.http.HttpServletRequest;

import nl.bitwalker.useragentutils.Version;

/**
 * This utility class helps to detect the browser which was used to access the server
 *
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 * @author Tomas Skripcak - enhancements
 */
public class BrowserDetection {

    public static enum UserAgent {
        IE,
        Opera,
        Gecko,
        AppleWebKit,
        Chrome,
    }

   /**
     * How many connections can this browser open simultaneously?
     * @param request The request so we can get at the user-agent header
     * @return The number of connections that we think this browser can take
     */
    public static int getConnectionLimit(HttpServletRequest request) {
        if (atLeast(request, UserAgent.IE, 8))
        {
            return 6;
        }
        else if (atLeast(request, UserAgent.AppleWebKit, 8))
        {
            return 4;
        }
        else if (atLeast(request, UserAgent.Opera, 9))
        {
            return 4;
        }
        else
        {
            return 2;
        }
    }

    /**
     * Does this web browser support comet?
     * @param request The request so we can get at the user-agent header
     * @return True if long lived HTTP connections are supported
     */
    public static boolean supportsComet(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");

        // None of the non-iPhone mobile browsers that I've tested support comet
        if (userAgent.contains("Symbian"))
        {
            return false;
        }

        // We need to test for other failing browsers here

        return true;
    }

    /**
     * Check that the user-agent string indicates some minimum browser level
     * @param request The browsers request
     * @param requiredUserAgent The UA required
     * @return true iff the browser matches the spec.
     */
    public static boolean atLeast(HttpServletRequest request, UserAgent requiredUserAgent) {
        return atLeast(request, requiredUserAgent, 0);
    }

    /**
     * Check that the user-agent string indicates some minimum browser level
     * @param request The browsers request
     * @param requiredUserAgent The UA required. Currently this is major version only
     * @param requiredEngineVersion The version required, or -1 if versions are not important
     * @return true iff the browser matches the spec.
     */
    public static boolean atLeast(HttpServletRequest request, UserAgent requiredUserAgent, int requiredEngineVersion) {
        String userAgent = request.getHeader("user-agent");
        int realVersion;

        switch (requiredUserAgent)
        {
            case IE:
                realVersion = getMajorVersionAssumingIE(userAgent);
                break;

            case Gecko:
                realVersion = getMajorVersionAssumingGecko(userAgent);
                break;

            case Opera:
                realVersion = getMajorVersionAssumingOpera(userAgent);
                break;

            case AppleWebKit:
                realVersion = getMajorVersionAssumingAppleWebKit(userAgent);
                break;

            default:
                throw new UnsupportedOperationException("Detection of " + requiredUserAgent + " is not supported yet.");
        }

        return realVersion >= requiredEngineVersion;
    }

    /**
     * This detection works better
     *
     * @param request
     * @param requiredUserAgent
     * @param requiredBrowserVersion
     * @return
     */
    public static boolean browserVersionAtLeast(HttpServletRequest request, UserAgent requiredUserAgent, int requiredBrowserVersion) {
        String userAgent = request.getHeader("user-agent");
        nl.bitwalker.useragentutils.UserAgent ua = nl.bitwalker.useragentutils.UserAgent.parseUserAgentString(userAgent);

        Version browserVersion = ua.getBrowserVersion();
        String browserName = ua.getBrowser().toString();

        int realVersion = Integer.parseInt(browserVersion.getMajorVersion());

        return realVersion >= requiredBrowserVersion;
    }

    /**
     * Check {@link #atLeast(HttpServletRequest, UserAgent)} for
     * {@link UserAgent#AppleWebKit}
     */
    private static int getMajorVersionAssumingAppleWebKit(String userAgent) {
        int webKitPos = userAgent.indexOf("AppleWebKit");
        if (webKitPos == -1)
        {
            return -1;
        }

        return parseNumberAtStart(userAgent.substring(webKitPos + 12));
    }

    /**
     * Check {@link #atLeast(HttpServletRequest, UserAgent)} for
     * {@link UserAgent#Opera}
     */
    private static int getMajorVersionAssumingOpera(String userAgent) {
        int operaPos = userAgent.indexOf("Opera");
        if (operaPos == -1)
        {
            return -1;
        }

        return parseNumberAtStart(userAgent.substring(operaPos + 6));
    }

    /**
     * Check {@link #atLeast(HttpServletRequest, UserAgent)} for
     * {@link UserAgent#Gecko}
     * Rendering engine checking
     */
    private static int getMajorVersionAssumingGecko(String userAgent) {
        int geckoPos = userAgent.indexOf(" Gecko/20");
        if (geckoPos == -1 || userAgent.contains("WebKit/"))
        {
            return -1;
        }

        return parseNumberAtStart(userAgent.substring(geckoPos + 7));
    }

    /**
     * Check {@link #atLeast(HttpServletRequest, UserAgent)} for
     * {@link UserAgent#IE}
     */
    private static int getMajorVersionAssumingIE(String userAgent) {
        int msiePos = userAgent.indexOf("MSIE ");
        if (msiePos == -1 || userAgent.contains("Opera"))
        {
            return -1;
        }

        return parseNumberAtStart(userAgent.substring(msiePos + 5));
    }

    /**
     * We've found the start of a sequence of numbers, what is it as an int?
     */
    private static int parseNumberAtStart(String numberString) {
        if (numberString == null || numberString.length() == 0) {
            return -1;
        }

        int endOfNumbers = 0;
        while (Character.isDigit(numberString.charAt(endOfNumbers))) {
            endOfNumbers++;
        }

        try {
            return Integer.parseInt(numberString.substring(0, endOfNumbers));
        }
        catch (NumberFormatException ex) {
            return -1;
        }
    }

    /**
     * This method is for debugging only
     */
    public static String getUserAgentDebugString(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");

        int version = getMajorVersionAssumingIE(userAgent);
        if (version != -1)
        {
            return "IE/" + version;
        }

        version = getMajorVersionAssumingGecko(userAgent);
        if (version != -1)
        {
            return "Gecko/" + version;
        }

        version = getMajorVersionAssumingAppleWebKit(userAgent);
        if (version != -1)
        {
            return "WebKit/" + version;
        }

        version = getMajorVersionAssumingOpera(userAgent);
        if (version != -1)
        {
            return "Opera/" + version;
        }

        return "Unknown: (" + userAgent + ")";
    }

}