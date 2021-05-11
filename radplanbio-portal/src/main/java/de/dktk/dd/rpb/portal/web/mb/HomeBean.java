/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2018 Tomas Skripcak
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

package de.dktk.dd.rpb.portal.web.mb;

import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.*;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import de.dktk.dd.rpb.core.domain.cms.SampleContent;

/**
 * Bean for home view
 *
 * @author tomas@skripcak.net
 * @since 01 Oct 2013
 */
@ManagedBean(name="mbHome")
@ViewScoped
public class HomeBean implements Serializable {

    //region Finals
    
    private static final long serialVersionUID = 1L;

    //endregion

    //region Members

    private List<SampleContent> rssContentList;

    //endregion

    //region Properties

    //region PieModel
//
//    public PieChartModel getPieModel() {
//        return pieModel;
//    }
//
    //endregion

    //region RSS Content List

    public List<SampleContent> getRssContentList() {
        return this.rssContentList;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void postConstruct() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            this.loadRssContent();
        }
        catch (Exception err) {
            context.addMessage(null, new FacesMessage("Error", err.getMessage()));
        }
    }

    //endregion

    //region Commands

    /**
     * Load RadPlanBio RSS content
     */
    public void loadRssContent() {
        FacesContext context = FacesContext.getCurrentInstance();
        
        HttpServletRequest req = (HttpServletRequest)context.getExternalContext().getRequest();
        String url = req.getRequestURL().toString();
        String rssFeedUrl = url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/api/rss";

        // Force https if external access
        if (rssFeedUrl.contains("uniklinikum-dresden.de")) {
            rssFeedUrl = rssFeedUrl.replace("http://", "https://");
        }

        // Do not require ssl handshake
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted( final X509Certificate[] chain, final String authType ) {
                }
                @Override
                public void checkServerTrusted( final X509Certificate[] chain, final String authType ) {
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            } };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init( null, trustAllCerts, new SecureRandom() );
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


            // All set up, we can get a resource through https now:
            final URLConnection urlCon = new URL(rssFeedUrl).openConnection();
            // Tell the url connection object to use our socket factory which bypasses security checks
            if (rssFeedUrl.contains("https://")) {
                ( (HttpsURLConnection) urlCon ).setSSLSocketFactory(sslSocketFactory);
            }

            final InputStream is = urlCon.getInputStream();

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(is));

            @SuppressWarnings("unchecked")
            List<SyndEntry> feedList = feed.getEntries();

            // Save only count requested
            // int feedSize = feedList.size();
            // int count = 300;  // desired number of feeds to retrieve
            // if (feedSize > count) {
            //      feedSize = count;
            // }

            this.rssContentList = new ArrayList<>();

            for (SyndEntry entry : feedList) {
                SampleContent rss = new SampleContent();

                rss.setTitle(entry.getTitle());
                rss.setCreatedDate(entry.getPublishedDate());
                rss.setSummary(entry.getDescription().getValue());

                //  Update
                this.rssContentList.add(rss);

                // Sort according to date (starting with the newest)
                if (this.rssContentList != null) {
                    Collections.sort(this.rssContentList, new Comparator<SampleContent>() {
                        public int compare(SampleContent rss1, SampleContent rss2) {
                            Date date1 = rss1.getCreatedDate();
                            Date date2 = rss2.getCreatedDate();

                            return (date2.compareTo(date1));
                        }
                    });
                }
            }

            context.addMessage(null, new FacesMessage("Success", "RSS feed content updated."));
        }
        catch (Exception err) {
            context.addMessage(null, new FacesMessage("Error", err.getMessage() + " URL: " + rssFeedUrl));
        }
    }

    //endregion

    //region Private Methods

//    private void createPieModel() {
//        pieModel = new PieChartModel();
//
//        pieModel.set("Brand 1", 540);
//        pieModel.set("Brand 2", 325);
//        pieModel.set("Brand 3", 702);
//        pieModel.set("Brand 4", 421);
//    }

    //endregion

}
